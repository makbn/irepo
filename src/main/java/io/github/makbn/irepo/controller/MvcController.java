package io.github.makbn.irepo.controller;

import io.github.makbn.irepo.crud.IPACRUD;
import io.github.makbn.irepo.exception.InternalServerException;
import io.github.makbn.irepo.exception.StorageException;
import io.github.makbn.irepo.exception.StorageFileNotFoundException;
import io.github.makbn.irepo.model.ApiError;
import io.github.makbn.irepo.model.IPA;
import io.github.makbn.irepo.service.IPAService;
import io.github.makbn.irepo.service.PlistService;
import io.github.makbn.irepo.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

@Controller("/")
public class MvcController {

    private final StorageService storageService;
    private final PlistService plistService;
    private final IPAService ipaService;
    private final IPACRUD ipacrud;

    @Autowired
    public MvcController(StorageService storageService, PlistService plistService, IPAService ipaService, IPACRUD ipacrud) {
        this.storageService = storageService;
        this.plistService = plistService;
        this.ipaService = ipaService;
        this.ipacrud = ipacrud;
    }

    @GetMapping("/{uuid}/install")
    public String install(Model model, @PathVariable("uuid") String uuid) throws IOException {
        IPA ipa = ipacrud.getByUUID(uuid, true);

       model.addAttribute("title", ipa.getTitle());
       model.addAttribute("name",ipa.getIpaInfo().getBundleName());
       model.addAttribute("package",ipa.getIpaInfo().getBundleIdentifier());
       model.addAttribute("uuid",uuid);
        return "install";
    }


    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

       /* model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));*/

        return "index";
    }

    @PostMapping("/v1/publish")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam final String name,
                                   @RequestParam final String email,
                                   RedirectAttributes redirectAttributes) {

        IPA ipa = storageService.store(file);
        ipa.setDeveloperName(name);
        ipa.setDeveloperEmail(email);
        try {
            ipaService.parse(ipa);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
        ipacrud.save(ipa);


        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/"+ipa.getUuid()+"/install";
    }


    @GetMapping("/v1/{ipa_uid}/plist")
    @ResponseBody
    public ResponseEntity<Resource> plist(@PathVariable("ipa_uid") String uuid) {

        IPA ipa = ipacrud.getByUUID(uuid, true);
        File plist;
        Resource resource;

        try {
            plist = plistService.getPlistFile(ipa);
            resource = new UrlResource(plist.toURI());
        } catch (IOException e) {
            throw new InternalServerException(e.getMessage());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/xml")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + ipa.getUuid()+".plist" + "\"").body(resource);
    }


    @GetMapping("/v1/{ipa_uid}/icon")
    @ResponseBody
    public ResponseEntity<Resource> icon(@PathVariable("ipa_uid") String uuid) {

        Resource file = storageService.loadAsResource(uuid, true);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/v1/{ipa_uid}/download")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable("ipa_uid") String uuid) {

        Resource file = storageService.loadAsResource(uuid, false);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + ".ipa" + "\"").body(file);
    }

    @ExceptionHandler({StorageFileNotFoundException.class,
            StorageException.class,
            InternalServerException.class})
    public ResponseEntity<?> handleStorageFileNotFound(RuntimeException exc) {
        int code;

        if(exc instanceof StorageFileNotFoundException){
            code = 100;
        }else if(exc instanceof  StorageException){
            code = 101;
        }else  {
            code = 102;
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .error(true)
                        .code(code)
                        .message(exc.getMessage())
                        .build());
    }
}
