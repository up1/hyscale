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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpRetryException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import io.hyscale.commons.exception.CommonErrorCode;
import io.hyscale.commons.exception.HyscaleException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogProcessor {

	private static final Logger logger = LoggerFactory.getLogger(LogProcessor.class);
	private static final Integer DEFAULT_LINES = 100;

	public void writeLogFile(InputStream is, String logFile) throws IOException,HyscaleException{
		if(is == null){
			throw new HyscaleException(CommonErrorCode.INVALID_INPUTSTREAM,null);
		}
		if (StringUtils.isBlank(logFile)){
			throw new HyscaleException(CommonErrorCode.FILE_NOT_FOUND,logFile);
		}
		// Start copying to file
		File targetFile = new File(logFile);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		Files.copy(is, Paths.get(logFile), StandardCopyOption.REPLACE_EXISTING);
	}

	public TailLogFile tailLogFile(File logFile, TailHandler handler) throws HyscaleException {
		if(handler == null){
			throw new HyscaleException(CommonErrorCode.INVALID_HANDLER,null);
		}
		if (logFile == null||!logFile.exists()) {
			logger.debug("logger file does'nt exists.");
			return null;
		}
		// Process file
		TailLogFile tailLog = new TailLogFile(logFile, handler);

		ThreadPoolUtil thread = ThreadPoolUtil.getInstance();
		thread.execute(tailLog);

		return tailLog;
	}

	public void readLogFile(File logFile, OutputStream os) throws HyscaleException{
		readLogFile(logFile, os, DEFAULT_LINES);
	}

	/*
	 * Filename, output stream, number of lines
	 */
	public void readLogFile(File logFile, OutputStream os, Integer lines)throws HyscaleException {
		if (logFile == null || !logFile.exists() || logFile.isDirectory()) {
			logger.error("File is Required for reading.");
			return;
		}
		if(os == null){
			throw new HyscaleException(CommonErrorCode.INVALID_OUTPUTSTREAM,null);
		}
		lines = lines != null ? lines : DEFAULT_LINES;
		int lineRead = 0;
		PrintStream printStream = new PrintStream(os);
		try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
			String logLine;
			while ((logLine = br.readLine()) != null && lineRead <= lines) {
				printStream.println(logLine);
				lineRead++;
			}
		} catch (FileNotFoundException e) {
			logger.error("File is required for logs");
		} catch (IOException e) {
			logger.error("Error while reading log file:{} ", logFile.getName());
		}
	}
}
