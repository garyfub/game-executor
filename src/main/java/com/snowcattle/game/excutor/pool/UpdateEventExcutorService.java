package com.snowcattle.game.excutor.pool;

import com.snowcattle.game.excutor.pool.excutor.SingleThreadEventExecutor;
import com.snowcattle.game.excutor.thread.DispatchThread;
import com.snowcattle.game.excutor.thread.SingleLockSupportUpdateThread;
import com.snowcattle.game.excutor.update.IUpdate;
import com.snowcattle.game.excutor.update.NullWeakUpUpdate;
import com.snowcattle.game.excutor.utils.ExecutorUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jwp on 2017/2/23.
 */
public class UpdateEventExcutorService implements IUpdateExcutor {

    private int excutorSize;

    private SingleThreadEventExecutor[] singleThreadEventExecutors;

    private final AtomicInteger idx = new AtomicInteger();

    private DispatchThread dispatchThread;

    public UpdateEventExcutorService(int excutorSize) {
        this.excutorSize = excutorSize;
    }

    public void start() {
        singleThreadEventExecutors = new SingleThreadEventExecutor[excutorSize];
        for (int i = 0; i < excutorSize; i++) {
            singleThreadEventExecutors[i] = new SingleThreadEventExecutor(dispatchThread);
        }
    }

    @Override
    public void stop() {
        for (int i = 0; i < excutorSize; i++) {
            ExecutorUtil.shutdownAndAwaitTermination(singleThreadEventExecutors[i], 60,
                    TimeUnit.MILLISECONDS);
        }
    }

    public SingleThreadEventExecutor getNext() {
        return singleThreadEventExecutors[idx.getAndIncrement() & singleThreadEventExecutors.length - 1];
    }

    @Override
    public void excutorUpdate(DispatchThread dispatchThread, IUpdate iUpdate, boolean initFlag) {
        SingleThreadEventExecutor singleThreadEventExecutor = getNext();
        singleThreadEventExecutor.excuteUpdate(iUpdate, initFlag);
    }

    public DispatchThread getDispatchThread() {
        return dispatchThread;
    }

    public void setDispatchThread(DispatchThread dispatchThread) {
        this.dispatchThread = dispatchThread;
    }
}