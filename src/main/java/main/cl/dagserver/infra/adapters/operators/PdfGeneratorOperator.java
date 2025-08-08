package main.cl.dagserver.infra.adapters.operators;

import org.json.JSONObject;
import java.io.OutputStream;
import com.nhl.dflib.DataFrame;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.FileOutputStream;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"inputHtmlUri","outputPdfFilePath"})
public class PdfGeneratorOperator extends OperatorStage {

	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		
		String inputFile = this.getInputProperty("inputHtmlUri");
		String outputFile = this.getInputProperty("outputPdfFilePath");
		try (OutputStream os = new FileOutputStream(outputFile)) {
	            PdfRendererBuilder builder = new PdfRendererBuilder();
	            builder.useFastMode();
	            builder.withUri(inputFile);
	            builder.toStream(os);
	            builder.run();
	            log.debug(this.getClass()+" end "+this.name);
	    		return DataFrameUtils.createStatusFrame("ok");
	    } catch (Exception e) {
	    	throw new DomainException(e);
		} 
		
	}
	

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.PdfGeneratorOperator");
		metadata.setType("PROCCESS");
		metadata.setParameter("inputHtmlUri", "text");
		metadata.setParameter("outputPdfFilePath", "text");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "htmlToPdf.jpg";
	}
}
