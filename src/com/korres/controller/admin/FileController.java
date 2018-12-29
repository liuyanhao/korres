package com.korres.controller.admin;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.korres.service.FileService;
import com.korres.util.JsonUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.korres.FileInfo;
import com.korres.Message;
import com.korres.FileInfo.FileInfoFileType;
import com.korres.FileInfo.FileInfoOrderType;

@Controller("adminFileController")
@RequestMapping({"/admin/file"})
public class FileController extends BaseController
{

  @Resource(name="fileServiceImpl")
  private FileService fileService;

  @RequestMapping(value={"/upload"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public void upload(FileInfoFileType fileType, MultipartFile file, HttpServletResponse response)
  {
    if (!this.fileService.isValid(fileType, file))
    {
      JsonUtils.toJson(response, "text/html; charset=UTF-8", Message.warn("admin.upload.invalid", new Object[0]));
    }
    else
    {
      String str = this.fileService.upload(fileType, file, false);
      if (str == null)
        JsonUtils.toJson(response, "text/html; charset=UTF-8", Message.warn("admin.upload.error", new Object[0]));
      HashMap localHashMap = new HashMap();
      localHashMap.put("url", str);
      JsonUtils.toJson(response, "text/html; charset=UTF-8", localHashMap);
    }
  }

  @RequestMapping(value={"/browser"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public List<FileInfo> browser(String path, FileInfoFileType fileType, FileInfoOrderType orderType)
  {
    return this.fileService.browser(path, fileType, orderType);
  }
}