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

public class _cls_ddos_monitor0 implements _callable{

public static PrintWriter pw; 
public static PrintWriter write_mal_cancel;
public static PrintWriter write_user_pay;
public static _cls_ddos_monitor0 root;

public static LinkedHashMap<_cls_ddos_monitor0,_cls_ddos_monitor0> _cls_ddos_monitor0_instances = new LinkedHashMap<_cls_ddos_monitor0,_cls_ddos_monitor0>();
static{
try{
pw = new PrintWriter("/Users/silvanzeller/Desktop/TCOMM/Master Thesis/BookingService/src/main/java/com/example/demo///output_ddos_monitor.txt");
write_mal_cancel = new PrintWriter ("/Users/silvanzeller/Desktop/TCOMM/Master Thesis/BookingService/src/main/java/com/example/demo///mal_users.txt");
write_user_pay = new PrintWriter ("/Users/silvanzeller/Desktop/TCOMM/Master Thesis/BookingService/src/main/java/com/example/demo///benign_users.txt");

root = new _cls_ddos_monitor0();
_cls_ddos_monitor0_instances.put(root, root);
  root.initialisation();
}catch(Exception ex)
{ex.printStackTrace();}
}

_cls_ddos_monitor0 parent; //to remain null - this class does not have a parent!
public static Boolean b;
int no_automata = 2;
 public int current_time =0 ;
 public int bookings =0 ;
 public int cancellations =0 ;
 public int late_cancellations =0 ;
 public boolean is_ddos =false ;
 public boolean system_reset =false ;
 public List <Booking >cancellations_list =new ArrayList <Booking >();
 public List <Booking >mal_cancellations_list =new ArrayList <Booking >();
 public List <Booking >user_pay_list =new ArrayList <Booking >();

public static void initialize(){}
//inheritance could not be used because of the automatic call to super()
//when the constructor is called...we need to keep the SAME parent if this exists!

public _cls_ddos_monitor0() {
}

public void initialisation() {
}

public static _cls_ddos_monitor0 _get_cls_ddos_monitor0_inst() { synchronized(_cls_ddos_monitor0_instances){
 return root;
}
}

public boolean equals(Object o) {
 if ((o instanceof _cls_ddos_monitor0))
{return true;}
else
{return false;}
}

public int hashCode() {
return 1;
}

public void _call(String _info, int... _event){
synchronized(_cls_ddos_monitor0_instances){
_performLogic_cancellations(_info, _event);
_performLogic_bookings(_info, _event);
}
}

public void _call_all_filtered(String _info, int... _event){

_cls_ddos_monitor1[] a1 = new _cls_ddos_monitor1[1];
synchronized(_cls_ddos_monitor1._cls_ddos_monitor1_instances){
a1 = _cls_ddos_monitor1._cls_ddos_monitor1_instances.keySet().toArray(a1);}
for (_cls_ddos_monitor1 _inst : a1)
if (_inst != null){
_inst._call(_info, _event); 
_inst._call_all_filtered(_info, _event);
}
}

public static void _call_all(String _info, int... _event){

_cls_ddos_monitor0[] a = new _cls_ddos_monitor0[1];
synchronized(_cls_ddos_monitor0_instances){
a = _cls_ddos_monitor0_instances.keySet().toArray(a);}
for (_cls_ddos_monitor0 _inst : a)

if (_inst != null) _inst._call(_info, _event);
}

public void _killThis(){
try{
if (--no_automata == 0){
synchronized(_cls_ddos_monitor0_instances){
_cls_ddos_monitor0_instances.remove(this);}
}
else if (no_automata < 0)
{throw new Exception("no_automata < 0!!");}
}catch(Exception ex){ex.printStackTrace();}
}

int _state_id_cancellations = 2;

public void _performLogic_cancellations(String _info, int... _event) {

if (0==1){}
else if (_state_id_cancellations==1){
		if (1==0){}
		else if ((_occurredEvent(_event,0/*confirm_cancel*/))){
		cancellations ++;
print_msg (cancellations +" cancellations (cancel -> cancel)");

		_state_id_cancellations = 1;//moving to state cancel
		_goto_cancellations(_info);
		}
		else if ((_occurredEvent(_event,0/*confirm_cancel*/)) && (is_ddos ==true )){
		cancellations ++;
print_msg (cancellations +" cancellations (cancel -> ddos)");

		_state_id_cancellations = 0;//moving to state ddos
		_goto_cancellations(_info);
		}
		else if ((_occurredEvent(_event,4/*system_reset*/))){
		reset_all_lists ();
print_msg ("resetting system from state cancel");

		_state_id_cancellations = 2;//moving to state start
		_goto_cancellations(_info);
		}
}
else if (_state_id_cancellations==2){
		if (1==0){}
		else if ((_occurredEvent(_event,0/*confirm_cancel*/))){
		cancellations ++;
print_msg (cancellations +" cancellations (start -> cancel)");

		_state_id_cancellations = 1;//moving to state cancel
		_goto_cancellations(_info);
		}
		else if ((_occurredEvent(_event,4/*system_reset*/))){
		reset_all_lists ();
print_msg ("resetting system from state start");

		_state_id_cancellations = 2;//moving to state start
		_goto_cancellations(_info);
		}
}
else if (_state_id_cancellations==0){
		if (1==0){}
		else if ((_occurredEvent(_event,0/*confirm_cancel*/))){
		cancellations ++;
print_msg (cancellations +" cancellations (ddos -> ddos)");

		_state_id_cancellations = 0;//moving to state ddos
		_goto_cancellations(_info);
		}
		else if ((_occurredEvent(_event,4/*system_reset*/))){
		reset_all_lists ();
is_ddos =false ;
print_msg ("resetting system from state ddos");

		_state_id_cancellations = 2;//moving to state start
		_goto_cancellations(_info);
		}
}
}

public void _goto_cancellations(String _info){
 String state_format = _string_cancellations(_state_id_cancellations, 1);
 if (state_format.startsWith("!!!SYSTEM REACHED BAD STATE!!!")) {
_cls_ddos_monitor0.pw.println("[cancellations]MOVED ON METHODCALL: "+ _info +" TO STATE::> " + state_format);
_cls_ddos_monitor0.pw.flush();
}
}

public String _string_cancellations(int _state_id, int _mode){
switch(_state_id){
case 1: if (_mode == 0) return "cancel"; else return "cancel";
case 2: if (_mode == 0) return "start"; else return "start";
case 0: if (_mode == 0) return "ddos"; else return "!!!SYSTEM REACHED BAD STATE!!! ddos "+new _BadStateExceptionddos_monitor().toString()+" ";
default: return "!!!SYSTEM REACHED AN UNKNOWN STATE!!!";
}
}
int _state_id_bookings = 4;

public void _performLogic_bookings(String _info, int... _event) {

if (0==1){}
else if (_state_id_bookings==4){
		if (1==0){}
		else if ((_occurredEvent(_event,2/*confirm_booking*/))){
		bookings ++;
print_msg (bookings +" bookings");

		_state_id_bookings = 4;//moving to state start
		_goto_bookings(_info);
		}
		else if ((_occurredEvent(_event,2/*confirm_booking*/)) && (bookings <cancellations )){
		print_msg ("undefined state - less bookings than cancellations");
bookings ++;

		_state_id_bookings = 3;//moving to state undefined
		_goto_bookings(_info);
		}
}
}

public void _goto_bookings(String _info){
 String state_format = _string_bookings(_state_id_bookings, 1);
 if (state_format.startsWith("!!!SYSTEM REACHED BAD STATE!!!")) {
_cls_ddos_monitor0.pw.println("[bookings]MOVED ON METHODCALL: "+ _info +" TO STATE::> " + state_format);
_cls_ddos_monitor0.pw.flush();
}
}

public String _string_bookings(int _state_id, int _mode){
switch(_state_id){
case 4: if (_mode == 0) return "start"; else return "start";
case 3: if (_mode == 0) return "undefined"; else return "!!!SYSTEM REACHED BAD STATE!!! undefined "+new _BadStateExceptionddos_monitor().toString()+" ";
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


void reset_all_lists() {
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


void merge_lists() {
print_msg("in merge_lists()");
for (Booking b : cancellations_list) {
System.out.println(b.toString());
mal_cancellations_list.add(b);
}
cancellations_list.clear();
}
}