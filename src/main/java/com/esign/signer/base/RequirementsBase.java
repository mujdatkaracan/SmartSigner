package com.esign.signer.base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.esign.signer.model.FilePropertiesModel;
 

/**
 * RequirementsBase.java sınıfı Folder ve/veya file yönetim süreçlerini barındırır. 
 * @author mujdat.karacan
 *
 */
public class RequirementsBase {

	public boolean createDirectory(String directoryPath) {
		boolean isError = false;
		Path path = Paths.get(directoryPath);
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				isError = true;
				// fail to create directory
				e.printStackTrace();
			}
		}

		return true;
	}

	public boolean createFile(String fileName, byte[] content) {
		boolean isError = false;
		File file = new File(fileName);
		if (!file.exists()) {
			if (file.mkdir()) {
				if (content.length > 0) {
					Path path = Paths.get(fileName);
					try {
						Files.write(path, content);
					} catch (IOException e) {
						isError = true;
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else {
				isError = true;
			}
		}
		return isError;
	}

	public FilePropertiesModel readFileProperties(String filePath) {

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

		File file = new File(classLoader.getResource(filePath).getFile());

		FilePropertiesModel fileproperties = new FilePropertiesModel();
		if (file.exists()) {
			fileproperties.setFileName(file.getName());
			fileproperties.setFilePath(filePath);
			try {
				fileproperties.setContent(Files.readAllBytes(file.toPath()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fileproperties.setExist(file.exists());

		return fileproperties;
	}
}
