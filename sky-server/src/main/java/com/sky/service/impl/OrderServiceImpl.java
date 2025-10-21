package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final UserMapper userMapper;
    private final WeChatPayUtil weChatPayUtil;
    private final WebSocketServer webSocketServer;
    @Value("${sky.shop.address}")
    private String shopAddress;
    @Value("${sky.baidu.ak}")
    private String ak;

    @Autowired
    public OrderServiceImpl(AddressBookMapper addressBookMapper, ShoppingCartMapper shoppingCartMapper, OrderMapper orderMapper, OrderDetailMapper orderDetailMapper, UserMapper userMapper, WeChatPayUtil weChatPayUtil, WebSocketServer webSocketServer) {
        this.addressBookMapper = addressBookMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.userMapper = userMapper;
        this.weChatPayUtil = weChatPayUtil;
        this.webSocketServer = webSocketServer;
    }

    /*
    * 提交订单
    * @pram ordersSubmitDTO
    * @return OrderSubmitVO
    * 注意：
    *  1. 需要操作 order，order_detail，address_book，shopping_cart 四张表
    *  2. 向订单表插入一条数据，结合address_book表和传递的参数
    *  3. 向订单明细表插入多条数据，结合shopping_cart表
    *  4. 清空用户购物车
    *  5. 封装并返回 OrderSubmitVO 对象
    * */
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 1. 判断地址是否为空以及是否超出配送范围
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if ( addressBook == null) {
            log.debug("Address book ID is null");
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        String address = (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail());
        //检查是否超出配送范围
        this.checkOutOfRange(address);
        // 2. 判断当前用户的购物车是否为空
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectListByUserId(userId);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            log.debug("Shopping cart is empty for user ID: {}", userId);
            throw new RuntimeException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 3. 插入订单表
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
          // 补全订单对象
          // 设置订单时间
        orders.setOrderTime(LocalDateTime.now());
          // 设置是否支付
        orders.setPayStatus(Orders.UN_PAID);
          // 设置订单状态为待支付
        orders.setStatus(Orders.PENDING_PAYMENT);
          // 设置订单号码
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
          // 设置phone，注意，这里的phone从第一步获取的addressBook中获取
        orders.setPhone(addressBook.getPhone());
          // 设置收货人，注意，这里的收货人从第一步获取的addressBook中获取
        orders.setConsignee(addressBook.getConsignee());
          // 设置当前用户的id，注意，这个参数非常关键，DTO中并没有这个参数
        orders.setUserId(userId);
        orderMapper.insert(orders); // 这里会使用主键回填
        // 4. 插入订单明细表
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(cart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId()); // 这里的orders.getId()是刚刚插入的订单的id
            return orderDetail;
        }).toList();
          // 批量插入订单明细
        orderDetailMapper.insertBatch(orderDetailList);
        // 5. 清空购物车
        shoppingCartMapper.deleteByUserId(userId);
        // 6. 封装并返回 OrderSubmitVO 对象
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);

        // 下单成功，调用WebSocketServer中的sendMessage方法
          // 1. 构建Message Map,包含type,orderId,content
        Map<String,String> messageMap = new HashMap<>();
        messageMap.put("type","1"); // 1 表示来单提醒
        messageMap.put("orderId",ordersDB.getId().toString());
        messageMap.put("content","Order Number: " + outTradeNo);
          // 2. Map -》 JSON
        String message = JSON.toJSONString(messageMap);
        webSocketServer.sendMessage(message);
    }

    /*
    * 查询历史订单，分页查询
    * @param page, pageSize, status
    * @return PageResult
    * 注意：当参数传递进去时，仍然构建OrdersPageQueryDTO对象,可以与管理端复用方法
    * */
    @Override
    public PageResult pageQuery4User(int page, int pageSize, Integer status) {
        // 开启分页
        PageHelper.startPage(page, pageSize);
        // 构建查询条件对象
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);
        // 执行查询
        Page<Orders> orders = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> orderVOList = new ArrayList<>();
        // 封装订单VO对象
        if (orders != null && orders.size() >0 ){
            orders.forEach(order -> {
                Long orderId = order.getId();
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order, orderVO);
                orderVO.setOrderDetailList(orderDetails);
                orderVOList.add(orderVO);
            });
        }

        return PageResult.builder()
                .total(orders.getTotal())
                .records(orderVOList)
                .build();
    }

    /*
    * 用户端 & 管理端 查询订单详情
    * @pram Long id
    * @return OrderVO
    * */
    @Override
    public OrderVO getOrderDetails(Long id) {
        // 1. 查询订单
        Orders order = orderMapper.getById(id);
        // 2. 查询订单明细
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(order.getId());
        // 3. 封装订单VO对象并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    /*
    * 用户取消订单
    * @pram Long id
    * @return
    * 注意：
    *  1.如果订单状态为带支付，status = 1，则直接修改订单状态为取消
    *  2.如果订单状态为已支付，status = 2，则修改订单状态之后需要调用微信支付的退款接口
    *  3.如果订单状态为已接点、已派送，status = 3、4，需要练习商家，在admin端进行取消订单
    *  4.如果订单状态为已完成、已取消，status = 5、6，不能取消订单
    * */
    @Transactional
    @Override
    public void userCancelById(Long id) throws Exception {
        // 1. 查询订单,判断订单是否存在
        Orders order = orderMapper.getById(id);
        if (order == null){
            log.debug("Order does not exist, order ID: {}", id);
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 2. 订单状态判断
        if (order.getStatus() > Orders.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 3.如果可以取消,修改订单状态为已取消,需要确认是否需要退款
        Orders updateOrder = new Orders();
        updateOrder.setId(id);
        updateOrder.setStatus(Orders.CANCELLED);
        updateOrder.setCancelTime(LocalDateTime.now());
        updateOrder.setCancelReason("User cancelled");
          // 判断是否需要修改为退款
        if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            // 需要退款，调用微信支付的退款接口
            weChatPayUtil.refund(order.getNumber(),order.getNumber(),new BigDecimal(0.01),new BigDecimal(0.01));
            updateOrder.setPayStatus(Orders.REFUND);
        }
          // 执行更新
        orderMapper.update(updateOrder);
    }

    /*
    * 用户点击再来一单
    * @pram Long id
    * @return
    * 注意：
    *  1.再来一单的本质上是将之前的订单数据重新插入到购物车表
    *  2.需要查询订单的详细信息，order_detail表
    *  3.将订单明细数据插入到购物车表, shopping_cart表
    * */
    @Override
    public void repetition(Long id) {
        // 1.查询订单明细
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        // 2.将订单明细数据插入到购物车表
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = orderDetails.stream().map(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).toList();
        // 3.批量插入
        shoppingCartMapper.insertBatch(shoppingCarts);
    }

    /*
    * 管理端条件查询订单
    * @pram OrdersPageQueryDTO
    * @return PageResult
    * 注意：
    *  1. 首先根据查询条件获得List<Orders>
    *  2. 需要将List<Orders>转换为List<OrderVO>,并填充上orderDishes属性
    *  3. 将List<OrderVO>封装到PageResult并返回
    * */
    @Transactional
    @Override
    public PageResult conditionSearch4Admin(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 1. 开启分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        // 2. 查询orders
        Page<Orders> orders = orderMapper.pageQuery(ordersPageQueryDTO);
        // 3. 封装OrderVO
        List<OrderVO> orderVOList = new ArrayList<>();
        orders.forEach(order -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            // 填充orderDishes属性
            orderVO.setOrderDishes(getOrderDishes(order.getId()));
            orderVOList.add(orderVO);
        });

        // 4. 封装并返回PageResult
        return PageResult.builder()
                .total(orders.getTotal())
                .records(orderVOList)
                .build();
    }
    /*
    * 附属方法
    *  1.查询order_detail,获取到List<OrderDetail>
    *  2.将List<OrderDetail>转换为String orderDishes
    * */
    private String getOrderDishes(Long orderId){
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
        // 拼接菜品名称，逗号分隔
        List<String> strings = orderDetails.stream().map(detail -> detail.getName() + "*" + detail.getNumber() + ";")
                .toList();
        return String.join(" ", strings);
    }

    /*
    * 分别统计不同状态下的订单数量
    * @pram
    * @return OrderStatisticsVO
    * 注意：主要统计的：status = 2 待接单，status = 3 已接单，status = 4 派送中
    * */
    @Override
    public OrderStatisticsVO countByStatus() {
        Integer toBeConfirmed = orderMapper.countByStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countByStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countByStatus(Orders.DELIVERY_IN_PROGRESS);
        // 封装并返回
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /*
    * 管理端接单
    * @pram OrdersConfirmDTO
    * @return
    * 接单的本质是修改订单状态为 status = 3 已接单
    * */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        // 1.获取订单id
        Long id = ordersConfirmDTO.getId();
        // 2. 构建订单对象
        Orders order = Orders.builder()
                .id(id)
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(order);
    }

    /*
    * 管理端拒绝订单
    * @pram OrdersRejectionDTO
    * @return
    * 注意：
    *  1.拒绝订单的本质上是修改订单状态为 status = 6 已取消
    *  2.需要拒绝理由
    *  3.如果订单的状态为已支付 status = 2，则需要调用微信支付的退款接口
    *  4.只能订单状态为待接单 status = 2
    * */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        // 1.获取订单id
        Long id = ordersRejectionDTO.getId();
        // 2.查询订单，判断订单是否存在
        Orders orders = orderMapper.getById(id);
        if (orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 3.判断订单状态是否为待接单
        if (!orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 4. 如果可以拒绝，修改订单状态为已取消
        Orders updateOrder = new Orders();
        updateOrder.setId(id);
        updateOrder.setStatus(Orders.CANCELLED);
        updateOrder.setCancelTime(LocalDateTime.now());
        updateOrder.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        // 5. 判断是否需要退款
        if (orders.getPayStatus().equals(Orders.PAID)){
            weChatPayUtil.refund(orders.getNumber(),orders.getNumber(),new BigDecimal(0.01),new BigDecimal(0.01));
            updateOrder.setPayStatus(Orders.REFUND);
        }
        // 6. 执行更新
        orderMapper.update(updateOrder);
    }

    /*
    * 管理端取消订单
    * @pram OrdersCancelDTO
    * @return
    * 注意：
    *  1.取消订单的本质上是修改订单状态为 status = 6 已取消
    *  2.需要取消理由
    *  3.如果订单的状态为已支付 status = 2，则需要调用微信支付的退款接口
    *  4.只能订单状态为待接单 status = 2，已接单 status = 3，派送中 status = 4
    * */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        // 1.获取订单id
        Long id = ordersCancelDTO.getId();
        // 2.查询订单，判断订单是否存在
        Orders orders = orderMapper.getById(id);
        if (orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 3.判断订单状态是否为待接单、已接单、派送中
        if (!orders.getStatus().equals(Orders.PAID) || !orders.getStatus().equals(Orders.CONFIRMED) || !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS) ){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 4. 如果可以取消，修改订单状态为已取消
        Orders updateOrder = new Orders();
        updateOrder.setId(id);
        updateOrder.setStatus(Orders.CANCELLED);
        updateOrder.setCancelReason(ordersCancelDTO.getCancelReason());
        updateOrder.setCancelTime(LocalDateTime.now());
        // 5. 判断是否需要退款
        if (orders.getPayStatus().equals(Orders.PAID)){
            weChatPayUtil.refund(orders.getNumber(),orders.getNumber(),new BigDecimal(0.01),new BigDecimal(0.01));
            updateOrder.setPayStatus(Orders.REFUND);
        }
        // 6. 执行更新
        orderMapper.update(updateOrder);
    }

    /*
    * 管理端派送订单,本质上与接单类似
    * @pram Long id
    * @return
    * 注意：
    *  1. 派送订单的本质是修改订单状态为 status = 4 派送中
    *  2. 只有订单状态为已接单 status = 3 才能派送
    * */
    @Override
    public void delivery(Long id) {
        // 1.获取订单对象
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 2.判断订单状态是否为已接单
        if (!orders.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 3.如果可以派送，修改订单状态为派送中
        Orders updateOrder = new Orders();
        updateOrder.setId(id);
        updateOrder.setStatus(Orders.DELIVERY_IN_PROGRESS);
        // 4.执行更新
        orderMapper.update(updateOrder);
    }

    /*
    * 管理端完成订单
    * @pram Long id
    * @return
    * 注意：
    *  1. 完成订单的本质是修改订单状态为 status = 5 已完成
    *  2. 只有订单状态为派送中 status = 4 才能完成订单
    * */
    @Override
    public void complete(Long id) {
        // 1. 获取订单对象，判断订单是否存在
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 2. 判断订单状态是否为派送中
        if (!orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 3. 如果可以完成订单，修改订单状态为已完成
        Orders updateOrder = new Orders();
        updateOrder.setId(id);
        updateOrder.setStatus(Orders.COMPLETED);
        updateOrder.setDeliveryTime(LocalDateTime.now());
        // 4. 执行更新
        orderMapper.update(updateOrder);
    }

    /**
    * 检查客户的收货地址是否超出配送范围,本项目以5KM为配送范围
    * @param address 用户收货地址
    */
    private void checkOutOfRange(String address) {
        Map map = new HashMap();
        map.put("address",shopAddress);
        map.put("output","json");
        map.put("ak",ak);

        //获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("Shop address parsing failed");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address",address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("Shipping address parsing failed");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);
        map.put("steps_info","0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("Route planning failed");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 5000){
            //配送距离超过5000米
            throw new OrderBusinessException("The delivery distance exceeds 5000 meters");
        }
    }


    /*
    * 客户催单
    * @pram Long id
    * @return
    * 流程：
    *  1. 首先查找订单是否存在，并且状态为status == 2,3,4
    *  2. 如果存在且状态正常，则发送催单通知，利用WebSocket发送消息
    * */
    @Override
    public void reminder(Long id) {
        Orders ordersDB = orderMapper.getById(id);
        // 判断是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 判断状态
        if (ordersDB.getStatus() < Orders.TO_BE_CONFIRMED || ordersDB.getStatus() > Orders.DELIVERY_IN_PROGRESS) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 发送催单通知
          // 1. 构建Message Map,包含type,orderId,content
        Map<String,String> messageMap = new HashMap<>();
        messageMap.put("type","2"); // 2 表示催单提醒
        messageMap.put("orderId",ordersDB.getId().toString());
        messageMap.put("content","Order Number: " + ordersDB.getNumber() + " is being reminded by the customer.");
          // 2. Map -》 JSON
        String message = JSON.toJSONString(messageMap);
        webSocketServer.sendMessage(message);
    }


}
