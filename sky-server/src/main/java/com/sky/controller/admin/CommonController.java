package com.sky.controller.admin;

import com.aliyuncs.exceptions.ClientException;
import com.kezhang.aliyunossoperator.AliyunOssUtil;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "CommonController", description = "Common Management,like file upload and download")
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {
    private final AliyunOssUtil aliyunOssUtil;
    @Autowired
    public CommonController(AliyunOssUtil aliyunOssUtil) {
        this.aliyunOssUtil = aliyunOssUtil;
    }
    /*
    * 阿里云OSS文件上传接口
    * @param MultipartFile file
    * @return Result<String> fileURL
    * */
    @Operation(summary = "Aliyun OSS file upload interface", description = "Aliyun OSS file upload interface")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("file:{}", file);
        // 调用工具类
        // 注意：servicePath 代表存储在oss上的路径，可以根据业务自行定义，需要以/结尾
        String servicePath = "dish/";
        String fileUrl = null;
        try {
            fileUrl = aliyunOssUtil.uploadFile(file, servicePath);
        } catch (ClientException | IOException e) {
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
        return Result.success(fileUrl);
    }

    /*
    * 阿里云OSS文件删除接口
    * @param String fileURL
    * @return Result<String>
    * 在前端点击取消新增或者更换图片时调用，避免残留OSS垃圾图片
    * */
    @Operation(summary = "Aliyun OSS file delete interface", description = "Aliyun OSS file delete interface")
    @DeleteMapping("/delete")
    public Result<String> delete(String fileURL){
        log.info("fileURL:{}", fileURL);
        // 调用工具类
        try {
            aliyunOssUtil.deleteFile(fileURL);
        } catch (ClientException e) {
            return Result.error(MessageConstant.DELETE_FAILED);
        }
        return Result.success();
    }

}
