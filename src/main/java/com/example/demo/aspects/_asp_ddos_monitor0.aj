
package com.example.demo.aspects;

import com.example.demo.constants.LocalConstants;
import com.example.demo.controllers.WebController;
import com.example.demo.domain.Booking;
import com.example.demo.domain.User;
import com.example.demo.configuration.InitialConfiguration;
import com.example.demo.repository.UserRepo;
import com.example.demo.repository.BookingRepo;
import com.example.demo.larva.*;
import java.time.*;
import java.util.List;
import java.util.ArrayList;


public aspect _asp_ddos_monitor0 {

public static Object lock = new Object();

boolean initialized = false;

after():(staticinitialization(*)){
if (!initialized){
	initialized = true;
	_cls_ddos_monitor0.initialize();
}
}
before ( Boolean b) : (call(* *.setCancel(..)) && args(b) && !cflow(adviceexecution()) && !cflow(within(larva.*))  && !(within(larva.*)) && if ((b ==true ))) {
synchronized(_asp_ddos_monitor0.lock){

_cls_ddos_monitor0 _cls_inst = _cls_ddos_monitor0._get_cls_ddos_monitor0_inst();
_cls_inst.b = b;
_cls_inst._call(thisJoinPoint.getSignature().toString(), 0/*confirm_cancel*/);
_cls_inst._call_all_filtered(thisJoinPoint.getSignature().toString(), 0/*confirm_cancel*/);
}
}
before () : (call(* *.updateDateAOP(..)) && args(*) && !cflow(adviceexecution()) && !cflow(within(larva.*))  && !(within(larva.*))) {

synchronized(_asp_ddos_monitor0.lock){

_cls_ddos_monitor0 _cls_inst = _cls_ddos_monitor0._get_cls_ddos_monitor0_inst();
_cls_inst._call(thisJoinPoint.getSignature().toString(), 4/*system_reset*/);
_cls_inst._call_all_filtered(thisJoinPoint.getSignature().toString(), 4/*system_reset*/);
}
}
before ( Boolean b) : (call(* *.setConfirm(..)) && args(b) && !cflow(adviceexecution()) && !cflow(within(larva.*))  && !(within(larva.*)) && if ((b ==true ))) {

synchronized(_asp_ddos_monitor0.lock){

_cls_ddos_monitor0 _cls_inst = _cls_ddos_monitor0._get_cls_ddos_monitor0_inst();
_cls_inst.b = b;
_cls_inst._call(thisJoinPoint.getSignature().toString(), 2/*confirm_booking*/);
_cls_inst._call_all_filtered(thisJoinPoint.getSignature().toString(), 2/*confirm_booking*/);
}
}
}