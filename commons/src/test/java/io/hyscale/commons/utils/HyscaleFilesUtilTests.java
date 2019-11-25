/**
 * Copyright 2019 Pramati Prism, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.hyscale.commons.utils;

import io.hyscale.commons.constants.TestConstants;
import io.hyscale.commons.constants.ToolConstants;
import io.hyscale.commons.exception.HyscaleException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class HyscaleFilesUtilTests {
    private static URL sampleFileUrl;
    private static String sampleFilePath;
    private static File sampleFile;
    private static String testDirPath;
    private static String testFilePath;
    private static File testDir;
    private static String testFileName;
    private static String sampleFileName;

    @BeforeAll
    public static void init() {
        sampleFileName = "sample.txt";
        sampleFileUrl = HyscaleFilesUtilTests.class.getClassLoader().getResource(sampleFileName);
        sampleFilePath = sampleFileUrl.getPath();
        testFileName = "testFile.txt";
        testDirPath = TestConstants.TMP_DIR + ToolConstants.FILE_SEPARATOR + "testDir" + ToolConstants.FILE_SEPARATOR;
        testFilePath = testDirPath + ToolConstants.FILE_SEPARATOR + testFileName;
        sampleFile = FileUtils.getFile(sampleFilePath);
        testDir = new File(testDirPath);
    }

    private File createFile(String filePath) {
        File newFile = new File(filePath);
        try {
            newFile.createNewFile();
        } catch (IOException e) {
        }
        return newFile;
    }

    private boolean deleteFile(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    private File createDirectory(String path) {
        new File(path).mkdirs();
        return new File(path);
    }

    private void deleteDirectory(File file) {
        if (file.exists()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
            }
        }
    }

    @Test
    public void createFileTest() throws HyscaleException {
        File file = HyscaleFilesUtil.createFile(testFilePath, "this is some file content");
        assertTrue(file.exists());
        assertTrue(testDir.isDirectory());
        assertTrue(testDir.exists());
        deleteDirectory(testDir);
    }


    @Test
    public void updateFileTest() throws HyscaleException {
        File file = createFile(testFilePath);
        long l1 = file.length();
        if (file.exists()) {
            long l2 = HyscaleFilesUtil.updateFile(testFilePath, "updated.").length();
            assertTrue(l1 < l2);
        }
        deleteDirectory(testDir);
    }

    @Test
    public void copyFileToDirTest() throws HyscaleException {
        HyscaleFilesUtil.copyFileToDir(sampleFile, testDir);
        assertTrue(new File(testDirPath,sampleFileName).exists());
        assertTrue(testDir.isDirectory());
        assertTrue(testDir.exists());
        deleteDirectory(testDir);
    }

    @Test
    public void copyFileTest() throws HyscaleException {
        File testFile = createFile(testFilePath);
        HyscaleFilesUtil.copyFile(sampleFile, testFile);
        assertTrue(testFile.length() == sampleFile.length() ? true : false);
       deleteDirectory(testDir);
    }

    @Test
    public void getFileNameTest() throws HyscaleException {
        assertEquals(sampleFileName, HyscaleFilesUtil.getFileName(sampleFilePath));
    }

    @Test
    public void clearDirectoryTest() throws Exception {
        testDir = createDirectory(testDirPath);
        File testFile = createFile(testFilePath);
        assertTrue(testDir.isDirectory());
        assertTrue(testDir.exists());
        assertTrue(testFile.exists());
        HyscaleFilesUtil.clearDirectory(testDirPath);
        assertFalse(testFile.exists());
        assertTrue(testDir.exists());
        deleteDirectory(testDir);
    }


    @Test
    public void deleteDirectoryTest() throws HyscaleException {
        testDir = createDirectory(testDirPath);
        assertTrue(testDir.exists());
        assertTrue(testDir.isDirectory());
        HyscaleFilesUtil.deleteDirectory(testDirPath);
        assertFalse(testDir.exists());

    }

    @AfterEach
    public void cleanUp(){
            deleteDirectory(testDir);
    }
}
