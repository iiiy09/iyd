package com.iyd.controller;

import com.iyd.common.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    @PostMapping("/upload")
    public R<?> uploadFile(@RequestParam MultipartFile file) {
        // In production: upload to OSS and return the URL
        String fileName = file.getOriginalFilename();
        String url = "/oss/files/" + System.currentTimeMillis() + "_" + fileName;
        return R.ok(url);
    }
}
