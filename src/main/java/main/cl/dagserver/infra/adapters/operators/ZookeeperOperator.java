package main.cl.dagserver.infra.adapters.operators;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.json.JSONArray;
import org.json.JSONObject;
import com.nhl.dflib.DataFrame;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"host","mode","path","timeout"},optionalv = {"xcom"})
public class ZookeeperOperator extends OperatorStage {

	private ZooKeeper zoo;
    private CountDownLatch connectionLatch = new CountDownLatch(1);
	
	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		DataFrame df = null;
		try {
			String host = this.args.getProperty("host");
			Integer timeout = Integer.parseInt(this.args.getProperty("timeout"));
			zoo = new ZooKeeper(host, timeout, new Watcher() {
	            public void process(WatchedEvent we) {
	                if (we.getState() == KeeperState.SyncConnected) {
	                    connectionLatch.countDown();
	                }
	            }
	        });	
			connectionLatch.await();
			String mode = this.args.getProperty("mode");
			String path = this.args.getProperty("path");
			
			if(mode.equals("READ")) {
				df = MetadataManager.jsonToDataFrame(new JSONArray(new String(zoo.getData(path, true, zoo.exists(path, true)))));
			} else if(mode.equals("INSERT")) {
				String xcomname = this.optionals.getProperty("xcom");
				if(!this.xcom.containsKey(xcomname)) {
					throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
				}
				DataFrame data = this.xcom.get(xcomname);	
				if (zoo.exists(path, false) == null) {
					zoo.create(path, MetadataManager.dataFrameToBytes(data), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				} else {
					zoo.setData(path, MetadataManager.dataFrameToBytes(data), zoo.exists(path, true).getVersion());	
				}
				df = OperatorStage.createStatusFrame("ok");
			} else {
				zoo.delete(path,  zoo.exists(path, true).getVersion());
				df = OperatorStage.createStatusFrame("ok");
			}
			zoo.close();
		} catch (Exception e) {
			throw new DomainException(e);
		}
		log.debug(this.getClass()+" end "+this.name);
		return df;
	}
	

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.ZookeeperOperator");
		metadata.setType("EXTERNAL");
		metadata.setParameter("host", "text");
		metadata.setParameter("mode", "list", Arrays.asList("READ","INSERT","DELETE"));
		metadata.setParameter("path", "text");
		metadata.setParameter("timeout", "number");
		metadata.setOpts("xcom","xcom");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "zookeeper.png";
	}
}
