package main.cl.dagserver.infra.adapters.confs;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import main.cl.dagserver.domain.exceptions.DomainException;

public class ZipFileVerificator {

	public static void verificationZipFile(ZipEntry ze,ZipFile zipFile) throws DomainException {
		int thresholdEntries = 10000;
		int thresholdSize = 1000000000; // 1 GB
		double thresholdRatio = 10;
		
		
		
		
		
		
		int totalSizeArchive = 0;
		int totalEntryArchive = 0;
		try(
				InputStream in = new BufferedInputStream(zipFile.getInputStream(ze));
				) {
				  totalEntryArchive ++;

				  int nBytes = -1;
				  byte[] buffer = new byte[2048];
				  double totalSizeEntry = 0;

				  while((nBytes = in.read(buffer)) > 0) { 
				      totalSizeEntry += nBytes;
				      totalSizeArchive += nBytes;
				      Long tmpv = ze.getCompressedSize();
				      double compressionRatio = totalSizeEntry / tmpv.doubleValue();
				      if(compressionRatio > thresholdRatio) {
				        // ratio between compressed and uncompressed data is highly suspicious, looks like a Zip Bomb Attack
				    	throw new DomainException(new Exception("invalid zip file"));
				      }
				  }

				  if(totalSizeArchive > thresholdSize) {
				      // the uncompressed data size is too much for the application resource capacity
					  throw new DomainException(new Exception("zip file invalid size"));
				  }

				  if(totalEntryArchive > thresholdEntries) {
				      // too much entries in this archive, can lead to inodes exhaustion of the system
					  throw new DomainException(new Exception("zip file invalid entries"));
				  }
			  } catch (Exception e) {
				  throw new DomainException(e);
			  }
	}

}
