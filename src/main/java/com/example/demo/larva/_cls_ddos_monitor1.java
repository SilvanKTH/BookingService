package com.example.demo.larva; 



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


import java.util.LinkedHashMap;
import java.io.PrintWriter;

public class _cls_ddos_monitor1 implements _callable{

public static LinkedHashMap<_cls_ddos_monitor1,_cls_ddos_monitor1> _cls_ddos_monitor1_instances = new LinkedHashMap<_cls_ddos_monitor1,_cls_ddos_monitor1>();

_cls_ddos_monitor0 parent;
public static Boolean b;
public static Integer cancel_latest;
public static Integer cancel_date;
public static String user;
public static Booking booking1;
public Booking booking;
int no_automata = 2;
 public final int CRITICAL_PERIOD =2 ;
 public final int CRITICAL_NO_ATTACKERS =2 ;

public static void initialize(){}
//inheritance could not be used because of the automatic call to super()
//when the constructor is called...we need to keep the SAME parent if this exists!

public _cls_ddos_monitor1( Booking booking) {
parent = _cls_ddos_monitor0._get_cls_ddos_monitor0_inst();
this.booking = booking;
}

public void initialisation() {
}

public static _cls_ddos_monitor1 _get_cls_ddos_monitor1_inst( Booking booking) { synchronized(_cls_ddos_monitor1_instances){
_cls_ddos_monitor1 _inst = new _cls_ddos_monitor1( booking);
if (_cls_ddos_monitor1_instances.containsKey(_inst))
{
_cls_ddos_monitor1 tmp = _cls_ddos_monitor1_instances.get(_inst);
 return _cls_ddos_monitor1_instances.get(_inst);
}
else
{
 _inst.initialisation();
 _cls_ddos_monitor1_instances.put(_inst,_inst);
 return _inst;
}
}
}

public boolean equals(Object o) {
 if ((o instanceof _cls_ddos_monitor1)
 && (booking == null || booking.equals(((_cls_ddos_monitor1)o).booking))
 && (parent == null || parent.equals(((_cls_ddos_monitor1)o).parent)))
{return true;}
else
{return false;}
}

public int hashCode() {
return (booking==null?1:booking.hashCode()) *(parent==null?1:parent.hashCode()) *1;
}

public void _call(String _info, int... _event){
synchronized(_cls_ddos_monitor1_instances){
_performLogic_late_cancellations(_info, _event);
_performLogic_restore_credit(_info, _event);
}
}

public void _call_all_filtered(String _info, int... _event){
}

public static void _call_all(String _info, int... _event){

_cls_ddos_monitor1[] a = new _cls_ddos_monitor1[1];
synchronized(_cls_ddos_monitor1_instances){
a = _cls_ddos_monitor1_instances.keySet().toArray(a);}
for (_cls_ddos_monitor1 _inst : a)

if (_inst != null) _inst._call(_info, _event);
}

public void _killThis(){
try{
if (--no_automata == 0){
synchronized(_cls_ddos_monitor1_instances){
_cls_ddos_monitor1_instances.remove(this);}
}
else if (no_automata < 0)
{throw new Exception("no_automata < 0!!");}
}catch(Exception ex){ex.printStackTrace();}
}

int _state_id_late_cancellations = 6;

public void _performLogic_late_cancellations(String _info, int... _event) {

if (0==1){}
else if (_state_id_late_cancellations==6){
		if (1==0){}
		else if ((_occurredEvent(_event,6/*late_cancellation*/)) && (((cancel_latest -cancel_date )<=CRITICAL_PERIOD )&&((cancel_latest -cancel_date )>=0 )&&(parent.late_cancellations <CRITICAL_NO_ATTACKERS ))){
		parent.late_cancellations ++;
/*print_msg (parent.late_cancellations +" late_cancellations");*/
parent.cancellations_list .add (booking );

		_state_id_late_cancellations = 6;//moving to state start
		_goto_late_cancellations(_info);
		}
		else if ((_occurredEvent(_event,6/*late_cancellation*/)) && (((cancel_latest -cancel_date )<=CRITICAL_PERIOD )&&((cancel_latest -cancel_date )>=0 ))){
		parent.is_ddos =true ;
parent.late_cancellations ++;
print_msg (parent.late_cancellations +" late_cancellations - critical state reached!");
merge_lists ();
parent.mal_cancellations_list .add (booking );

		_state_id_late_cancellations = 5;//moving to state ddos
		_goto_late_cancellations(_info);
		}
		else if ((_occurredEvent(_event,4/*system_reset*/))){
		/*print_msg ("resetting system from state start (Booking)"+booking .toString ());*/

		_state_id_late_cancellations = 6;//moving to state start
		_goto_late_cancellations(_info);
		}
}
else if (_state_id_late_cancellations==5){
		if (1==0){}
		else if ((_occurredEvent(_event,6/*late_cancellation*/)) && (((cancel_latest -cancel_date )<=CRITICAL_PERIOD )&&((cancel_latest -cancel_date )>=0 ))){
		parent.late_cancellations ++;
/*print_msg (parent.late_cancellations +" late_cancellations");*/
parent.mal_cancellations_list .add (booking );

		_state_id_late_cancellations = 5;//moving to state ddos
		_goto_late_cancellations(_info);
		}
		else if ((_occurredEvent(_event,4/*system_reset*/))){
		print_msg ("resetting system from state ddos (Booking)");

		_state_id_late_cancellations = 6;//moving to state start
		_goto_late_cancellations(_info);
		}
}
}

public void _goto_late_cancellations(String _info){
 String state_format = _string_late_cancellations(_state_id_late_cancellations, 1);
 if (state_format.startsWith("!!!SYSTEM REACHED BAD STATE!!!")) {
_cls_ddos_monitor0.pw.println("[late_cancellations]MOVED ON METHODCALL: "+ _info +" TO STATE::> " + state_format);
_cls_ddos_monitor0.pw.flush();
}
}

public String _string_late_cancellations(int _state_id, int _mode){
switch(_state_id){
case 6: if (_mode == 0) return "start"; else return "start";
case 5: if (_mode == 0) return "ddos"; else return "!!!SYSTEM REACHED BAD STATE!!! ddos "+new _BadStateExceptionddos_monitor().toString()+" ";
default: return "!!!SYSTEM REACHED AN UNKNOWN STATE!!!";
}
}
int _state_id_restore_credit = 7;

public void _performLogic_restore_credit(String _info, int... _event) {

if (0==1){}
else if (_state_id_restore_credit==7){
		if (1==0){}
		else if ((_occurredEvent(_event,8/*user_payment*/))){
		parent.user_pay_list .add (booking );
/*print_msg ("Restoring credit for user "+booking .getName ());*/

		_state_id_restore_credit = 7;//moving to state start
		_goto_restore_credit(_info);
		}
}
}

public void _goto_restore_credit(String _info){
 String state_format = _string_restore_credit(_state_id_restore_credit, 1);
 if (state_format.startsWith("!!!SYSTEM REACHED BAD STATE!!!")) {
_cls_ddos_monitor0.pw.println("[restore_credit]MOVED ON METHODCALL: "+ _info +" TO STATE::> " + state_format);
_cls_ddos_monitor0.pw.flush();
}
}

public String _string_restore_credit(int _state_id, int _mode){
switch(_state_id){
case 7: if (_mode == 0) return "start"; else return "start";
default: return "!!!SYSTEM REACHED AN UNKNOWN STATE!!!";
}
}

public boolean _occurredEvent(int[] _events, int event){
for (int i:_events) if (i == event) return true;
return false;
}

void print_msg(String msg) {
System.out.println("LARVA LOG: >>"+msg);
}

/*
void reset_all_lists() {
print_msg("in reset_all_lists()");
late_cancellations = 0;

if (cancellations_list.size() > 0) {
cancellations_list.clear();
}

if (mal_cancellations_list.size() > 0) {
print_msg("mal_cancellations size: "+mal_cancellations_list.size());
for (Booking b : mal_cancellations_list) {
write_mal_cancel.print(b.getName()+"#");
}
mal_cancellations_list.clear();
}
write_mal_cancel.println("\n---");
write_mal_cancel.flush();

if (user_pay_list.size() > 0) {
print_msg("user_pay size: "+ user_pay_list.size());
for (Booking b : user_pay_list) {
write_user_pay.print(b.getName()+"#");
}
user_pay_list.clear();
}
write_user_pay.println("\n+++");
write_user_pay.flush();
}
*/

void merge_lists() {
/*print_msg("in merge_lists()");*/
for (Booking b : parent.cancellations_list) {
System.out.println(b.toString());
parent.mal_cancellations_list.add(b);
}
parent.cancellations_list.clear();
}
}