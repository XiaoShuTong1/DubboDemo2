package carrot.rpc.test;

import carrot.rpc.common.Invoker;
import carrot.rpc.consumer.cluster.Cluster;
import carrot.rpc.consumer.cluster.FailFastCluster;
import carrot.rpc.consumer.directory.Directory;
import carrot.rpc.consumer.directory.StaticDirectory;
import carrot.rpc.consumer.loadbalancer.RandomLoadBalancer;
import carrot.rpc.consumer.proxy.JdkProxyFactory;
import carrot.rpc.consumer.Refer;
import carrot.rpc.consumer.RpcInvoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CunsumerTest {

    private final static Logger logger = LoggerFactory.getLogger(CunsumerTest.class);

    public static void main(String[] args) throws InterruptedException {
        Invoker invoker1 =new RpcInvoker("127.0.0.1",8666);
        Invoker invoker2 =new RpcInvoker("127.0.0.1",8668);
        Directory directory = new StaticDirectory();
        List<Invoker> invokers = new ArrayList<Invoker>();
        ((StaticDirectory) directory).setInvokers(invokers);
        invokers.add(invoker1);
        invokers.add(invoker2);

        Cluster cluster = new FailFastCluster();
        ((FailFastCluster) cluster).setLoadBalancer(new RandomLoadBalancer());
        ((FailFastCluster) cluster).setDirectory(directory);

        JdkProxyFactory jdkProxyFactory = new JdkProxyFactory();

        Refer<TestService> refer = new Refer<TestService>();
        refer.setCluster(cluster);
        refer.setInterfaceName("TestService");
        refer.setJdkProxyFactory(jdkProxyFactory);
        refer.setInterfaceClass(new Class[]{TestService.class});
        refer.init();

        TestService testService = refer.getRef();
        for(int i=0;i<1000;i++){
            String world = testService.hello("world");
            logger.info(world);
            Thread.sleep(1000);
        }


    }
}
