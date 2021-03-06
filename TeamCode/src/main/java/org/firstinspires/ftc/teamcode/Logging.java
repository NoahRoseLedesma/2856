package org.firstinspires.ftc.teamcode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by matt on 12/16/15.
 */
public class Logging {

	File file;

	public Logging(String filename)
	{
		file = new File("/sdcard/Pictures",filename);
	}

	public void write(String line) {
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file, true);
			line += "\n";
			outputStream.write(line.getBytes());
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
