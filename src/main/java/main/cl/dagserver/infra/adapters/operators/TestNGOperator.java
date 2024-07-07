package main.cl.dagserver.infra.adapters.operators;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import org.testng.TestNG;
import org.testng.xml.SuiteXmlParser;
import org.testng.xml.XmlSuite;

import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.DagPathClassLoadHelper;

@Operator(args={"classpath","reportOutput","testngXmlFiles"})
public class TestNGOperator extends OperatorStage {

	private DagPathClassLoadHelper helper = new DagPathClassLoadHelper();
	
	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		try {
			List<String> fJar = new ArrayList<>();
			String fpath = this.args.getProperty("classpath");
			this.searchJarFiles(new File(fpath),fJar );
			List<URI> list = new ArrayList<>();
			for (Iterator<String> iterator = fJar.iterator(); iterator.hasNext();) {
				String jarpath = iterator.next();
				list.add(new File(jarpath).toURI());
			}
			
			TestNG testng = new TestNG();
			testng.setOutputDirectory(this.args.getProperty("reportOutput"));
			testng.setVerbose(10);
	        ClassLoader classloader = helper.getClassLoader(list);
	        testng.addClassLoader(classloader);
	        
	        String[] testngs = this.args.getProperty("testngXmlFiles").split(";");
	        List<XmlSuite> suites = new ArrayList<>();
	        for (int i = 0; i < testngs.length; i++) {
				String string = testngs[i];
				InputStream resourceURL = classloader.getResourceAsStream(string);
		        SuiteXmlParser suiteXmlParser = new SuiteXmlParser();
		        XmlSuite suite = suiteXmlParser.parse(string, resourceURL, true);
		        suites.add(suite);
			}
			testng.setXmlSuites(suites);
	        testng.run();
			log.debug(this.args);
			log.debug(this.getClass()+" end "+this.name);
			return DataFrameUtils.createFrame("statusCode", testng.getStatus());
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	
	private void searchJarFiles(File directorio, List<String> archivosJar) {
        File[] archivos = directorio.listFiles();

        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isFile() && archivo.getName().endsWith(".jar")) {
                    archivosJar.add(archivo.getAbsolutePath());
                } else if (archivo.isDirectory()) {
                	searchJarFiles(archivo, archivosJar);
                }
            }
        }
    }
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.TestNGOperator");
		metadata.setType("PROCCESS");
		metadata.setParameter("classpath", "text");
		metadata.setParameter("reportOutput", "text");
		metadata.setParameter("testngXmlFiles", "text");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "testng.png";
	}
}
