package com.tpv.epgglobo;

import com.tpv.epgglobo.model.Program;

public interface AlarmSchedulerInterface {
    void schedule(Program item);
    void cancel(Program item);
}
