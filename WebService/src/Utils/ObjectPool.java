package Utils;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//https://www.javacodegeeks.com/2013/08/simple-and-lightweight-pool-implementation.html
class PoolItem<T> {
    public int idx;
    private T item;
    private Date createDT;

    public PoolItem(T item) {
        this.idx = new Random().nextInt(100);
        this.item = item;
        this.createDT = new Date();
    }

    public T getItem() {
        return item;
    }

    public Date getCreateDT() {
        return createDT;
    }
}

public abstract class ObjectPool<T> {

    private ConcurrentLinkedQueue<PoolItem> availPool = new ConcurrentLinkedQueue<PoolItem>();
    private ScheduledExecutorService executorService = null;

    public ObjectPool(final Integer minCount, final Integer maxCount, final Integer maxLiveDur) {

        if ((minCount != null) || (maxCount != null) || (maxLiveDur != null)) {
            int validationInterval = 2;
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    if (maxLiveDur != null) {
                        for (PoolItem<T> poolItem : availPool) {
                            if (new Date().getTime() - poolItem.getCreateDT().getTime() >= maxLiveDur * 1000) {
                                availPool.remove(poolItem);
                            }
                        }
                    }
                    int size = availPool.size();
                    if ((minCount != null) && (size < minCount)) {
                        int sizeToBeAdded = minCount - size;
                        for (int i = 0; i < sizeToBeAdded; i++) {
                            try {
                                returnObject(createObjectSync());
                            } catch (Exception e) {
                            }
                        }
                    } else if ((maxCount != null) && (size > maxCount)) {
                        int sizeToBeRemoved = size - maxCount;
                        for (int i = 0; i < sizeToBeRemoved; i++) {
                            availPool.poll();
                            //System.out.println("Poll");
                        }
                    }
                }
            }, validationInterval, validationInterval, TimeUnit.SECONDS);
        }
    }


    public T borrowObject() throws Exception {
        PoolItem poolItem = availPool.poll();
        if (poolItem == null) {
            return createObjectSync();
        } else {
            T object = (T) poolItem.getItem();
            if (!isValid(object))
                return createObjectSync();
            else
                return object;
        }

    }

    public void returnObject(T object) {
        if (object == null) {
            return;
        }
        this.availPool.offer(new PoolItem(object));
    }

    public void clear() {
        availPool.clear();
    }

    public void destroy() {
        if (executorService != null)
            executorService.shutdown();
        while (!executorService.isTerminated()) {
        }
        clear();
    }

    public synchronized boolean isValid(T object) {
        return true;
    }

    private synchronized T createObjectSync() throws Exception {
        return createObject();
    }

    protected abstract T createObject() throws Exception;

}

