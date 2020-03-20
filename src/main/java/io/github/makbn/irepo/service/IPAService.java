package io.github.makbn.irepo.service;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.dd.plist.*;
import io.github.makbn.irepo.exception.InternalServerException;
import io.github.makbn.irepo.model.IPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IPAService {

    private final StorageService storageService;

    @Autowired
    public IPAService(StorageService storageService) {
        this.storageService = storageService;
    }

    public String getLastIconFileName(NSDictionary dict, String identifier) {
        NSDictionary primaryIcon = (NSDictionary) dict.get(identifier);
        NSDictionary iconFiles = (NSDictionary) primaryIcon.get("CFBundlePrimaryIcon");
        NSObject [] files = ((NSArray) iconFiles.get("CFBundleIconFiles")).getArray();

        String name = null;

        for(NSObject file : files) {
            name = file.toString();
        }

        return name;
    }

    public void parse(IPA ipa) throws Exception {

        File zipFile = storageService.load(ipa.getUuid()).toFile();
        ipa.getIpaInfo().setSize(zipFile.length());

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {
           if (entry.getName().endsWith(".app/Info.plist")) {
                System.out.println(entry.getName());
                loadPlist(ipa, entry, zipIn);
                zipIn.close();
                break;
            }

            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }

        zipIn = new ZipInputStream(new FileInputStream(zipFile));
        entry = zipIn.getNextEntry();

        while (entry != null) {
            if(entry.getName().contains(ipa.getIconName())){
                System.out.println(entry.getName());
                loadIcon(ipa, entry, zipIn);
                zipIn.closeEntry();
                break;
            }

            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();

        if (ipa.getIpaInfo().getBundleIdentifier() == null) {
            throw new InternalServerException("cant load bundle identifier from ipa file!");
        }
    }

    private void loadPlist(IPA ipa, ZipEntry entry, ZipInputStream in) throws Exception {
        NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(in);
        ipa.getIpaInfo().setBundleName(rootDict.get("CFBundleName").toString());
        ipa.getIpaInfo().setBundleVersion(rootDict.get("CFBundleShortVersionString").toString());
        ipa.getIpaInfo().setBundleIdentifier(rootDict.get("CFBundleIdentifier").toString());
        ipa.getIpaInfo().setMinOsVersion(rootDict.get("MinimumOSVersion").toString());
        ipa.setTitle(rootDict.get("CFBundleDisplayName").toString());
        ipa.setIconName(getLastIconFileName(rootDict,"CFBundleIcons"));
    }

    private void loadIcon(IPA ipa, ZipEntry entry, ZipInputStream in) throws IOException {
        System.out.println(entry.getName());
        storageService.saveIcon(ipa, in);
    }
}