import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 Этот класс выполняет роль певца.
 Что он по факту делает- он считывает строчки из песни, если она его, то он поет,
 если нет, то нотифает тот тред, чьей эта строчка является и после этого засыпает.
 Если тред встречает строку, которую должны петь оба треда, то он выводит строку,
 включает режим пения вместе, нотифает все треды и засыпает.
 Если песня кончилась, то он просто нотифает все треды и завершает работу
 */

public class Singer extends Thread {

    String singerName;
    Song song;
    Condition condition;
    Lock lock;

    private static boolean togetherSing=false;

    LinkedHashMap<String,Condition> conditionMap;

    public Singer(String singerName_, Song song_, Condition condition_, Lock lock_, LinkedHashMap<String, Condition> conditionMap_) {
        singerName = singerName_;
        song=song_;
        condition=condition_;
        lock=lock_;
        conditionMap=conditionMap_;
    }

    @Override
    public void run(){
        while (true){
            String[] string=song.getCurrString();
            if(string[0].equals("")){

                lock.lock();
                for (Map.Entry<String, Condition> entry : conditionMap.entrySet()) {
                    String key=entry.getKey();
                    Condition condition=entry.getValue();
                    if(!key.equals(singerName)){
                        condition.signal();
                    }
                }
                lock.unlock();
                break;
            }
            if(string[0].equals("Sonny, Cher")){

                togetherSing=true;
                lock.lock();
                System.out.println(singerName+ " :"+ string[1]);
                for (Map.Entry<String, Condition> entry : conditionMap.entrySet()) {
                    String key=entry.getKey();
                    Condition condition=entry.getValue();
                    if(!key.equals(singerName)){
                        condition.signal();
                    }
                }
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(togetherSing){
                    togetherSing=false;
                    song.increaseStrNumb();
                }
                lock.unlock();

                continue;
            }
            if(string[0].equals(singerName)){
                lock.lock();
                System.out.println(singerName + ": " + string[1]);
                song.increaseStrNumb();
                lock.unlock();
                continue;

            }
            lock.lock();
            conditionMap.get(string[0]).signal();
            try {
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }
    }

}
