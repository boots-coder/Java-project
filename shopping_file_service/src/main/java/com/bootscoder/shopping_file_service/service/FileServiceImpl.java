package com.bootscoder.shopping_file_service.service;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.bootscoder.shopping_common.result.MyException;
import com.bootscoder.shopping_common.result.ResultCode;
import com.bootscoder.shopping_common.service.FileService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;

@DubboService
public class FileServiceImpl implements FileService {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Value("${fdfs.fileUrl}")
    private String fileUrl; // Nginx访问FastDFS中文件的路径

    @Override
    public String uploadImage(byte[] fileBytes, String fileName) {
        if (fileBytes.length != 0) {
            try {
                // 1.将文件的字节数组转为输入流
                ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
                // 2.获取文件的后缀名
                String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                // 3.上传文件
                StorePath storePath = fastFileStorageClient.uploadFile(inputStream, inputStream.available(), fileSuffix, null);
                // 4.返回图片路径
                String imageUrl = fileUrl + "/"+storePath.getFullPath();
                return imageUrl;
            }catch (Exception e){
                throw new MyException(ResultCode.UPLOAD_FILE_ERROR);
            }
        } else {
            throw new MyException(ResultCode.UPLOAD_FILE_ERROR);
        }
    }

    @Override
    public void delete(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }
}
