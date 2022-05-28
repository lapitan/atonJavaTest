import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) {
        Song song=new Song();

        Lock lock= new ReentrantLock();
        Condition sonnyCondition=lock.newCondition();

        Condition cherCondition= lock.newCondition();

        LinkedHashMap<String,Condition> conditionMap=new LinkedHashMap<>();
        conditionMap.put("Sonny",sonnyCondition);
        conditionMap.put("Cher",cherCondition);

        Singer sonny=new Singer("Sonny",song,sonnyCondition, lock,conditionMap);
        sonny.setName("Sonny");

        Singer cher=new Singer("Cher",song,cherCondition, lock,conditionMap);
        cher.setName("Cher");


        sonny.start();
        cher.start();

    }

}
