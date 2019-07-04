
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


public aspect _asp_ddos_monitor1 {

boolean initialized = false;

after():(staticinitialization(*)){
if (!initialized){
	initialized = true;
	_cls_ddos_monitor1.initialize();
}
}
before ( Boolean b,Booking booking1) : (call(* Booking.setPayment(..)) && target(booking1) && args(b) && !cflow(adviceexecution()) && !cflow(within(larva.*))  && !(within(larva.*)) && if ((b ==true ))) {

synchronized(_asp_ddos_monitor0.lock){
Booking booking;
String user;
booking =booking1 ;
user =booking1 .getName ();

_cls_ddos_monitor1 _cls_inst = _cls_ddos_monitor1._get_cls_ddos_monitor1_inst( booking);
_cls_inst.b = b;
_cls_inst.booking1 = booking1;
_cls_ddos_monitor1.user = user;
_cls_inst._call(thisJoinPoint.getSignature().toString(), 8/*user_payment*/);
_cls_inst._call_all_filtered(thisJoinPoint.getSignature().toString(), 8/*user_payment*/);
}
}
before ( Boolean b,Booking booking1) : (call(* Booking.setCancel(..)) && target(booking1) && args(b) && !cflow(adviceexecution()) && !cflow(within(larva.*))  && !(within(larva.*)) && if ((b ==true ))) {

	Long startTimeAspectNano = System.nanoTime();
synchronized(_asp_ddos_monitor0.lock){
	
Booking booking;
Integer cancel_latest;
Integer cancel_date;
booking =booking1 ;
cancel_latest =booking1 .getCancelLatest ();
cancel_date =booking1 .getCancelDate ();

_cls_ddos_monitor1 _cls_inst = _cls_ddos_monitor1._get_cls_ddos_monitor1_inst( booking);
_cls_inst.b = b;
_cls_inst.booking1 = booking1;
_cls_ddos_monitor1.cancel_latest = cancel_latest;
_cls_ddos_monitor1.cancel_date = cancel_date;
_cls_inst._call(thisJoinPoint.getSignature().toString(), 6/*late_cancellation*/);
_cls_inst._call_all_filtered(thisJoinPoint.getSignature().toString(), 6/*late_cancellation*/);
	
}
	Long stopTimeAspectNano = System.nanoTime();
	Long aspectTime = stopTimeAspectNano - startTimeAspectNano;
	InitialConfiguration.aspectTimes.add(aspectTime);

}
}