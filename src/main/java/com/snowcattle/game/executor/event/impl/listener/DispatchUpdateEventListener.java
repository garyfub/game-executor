package com.snowcattle.game.executor.event.impl.listener;

import com.snowcattle.game.executor.update.entity.IUpdate;
import com.snowcattle.game.executor.event.CycleEvent;
import com.snowcattle.game.executor.event.EventParam;
import com.snowcattle.game.executor.event.common.IEvent;
import com.snowcattle.game.executor.event.impl.event.FinishEvent;
import com.snowcattle.game.executor.update.pool.IUpdateExcutor;
import com.snowcattle.game.executor.update.service.UpdateService;
import com.snowcattle.game.executor.update.thread.dispatch.DispatchThread;
import com.snowcattle.game.executor.common.utils.Constants;

/**
 * Created by jiangwenping on 17/1/11.
 */
public class DispatchUpdateEventListener extends UpdateEventListener {
    private DispatchThread dispatchThread;
    private UpdateService updateService;
    public DispatchUpdateEventListener(DispatchThread dispatchThread, UpdateService updateService) {
        this.dispatchThread = dispatchThread;
        this.updateService = updateService;
    }


    public void fireEvent(IEvent event) {
//        if(Loggers.utilLogger.isDebugEnabled()){
//            Loggers.utilLogger.debug("处理update");
//        }
        super.fireEvent(event);

        //提交执行线程
        CycleEvent updateEvent = (CycleEvent) event;
        EventParam[] eventParams = event.getParams();
        for(EventParam eventParam: eventParams) {
            IUpdate iUpdate = (IUpdate) eventParam.getT();
            boolean aliveFlag = updateEvent.isUpdateAliveFlag();
            if (aliveFlag) {
                IUpdateExcutor iUpdateExcutor = dispatchThread.getiUpdateExcutor();
                iUpdateExcutor.excutorUpdate(dispatchThread, iUpdate, updateEvent.isInitFlag(), updateEvent.getUpdateExcutorIndex());
            } else {
                FinishEvent finishEvent = new FinishEvent(Constants.EventTypeConstans.finishEventType, iUpdate.getId(), eventParams);
                dispatchThread.addFinishEvent(finishEvent);
            }
        }

    }
}