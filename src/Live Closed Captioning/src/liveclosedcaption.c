#include <pebble.h>

Window *window;
TextLayer *text_layer;
char* dispText; //what will be displayed
char* incomingText; //coming from android
char* transformText; //big kahuna, dump truck, salad bowl, mixing pallette

enum {
	STATUS_KEY = 0,
	MESSAGE_KEY = 1
};

// Write message to buffer & send
void send_message(void){
	DictionaryIterator *iter;

	app_message_outbox_begin(&iter);
	dict_write_uint8(iter, STATUS_KEY, 0x1);
	dict_write_cstring(iter, MESSAGE_KEY, "Hi Phone, I'm a Pebble!");
    
	dict_write_end(iter);
  	app_message_outbox_send();
}

// Called when a message is received from PebbleKitJS
static void in_received_handler(DictionaryIterator *received, void *context) {
	Tuple *tuple;

	tuple = dict_find(received, STATUS_KEY);
	if(tuple) {
		APP_LOG(APP_LOG_LEVEL_DEBUG, "Received Status: %d", (int)tuple->value->uint32);
	}

	tuple = dict_find(received, MESSAGE_KEY);
	if(tuple) {
		APP_LOG(APP_LOG_LEVEL_DEBUG, "Received Message: %s", tuple->value->cstring);
	}}

// Called when an incoming message from PebbleKitJS is dropped
static void in_dropped_handler(AppMessageResult reason, void *context) {
}

// Called when PebbleKitJS does not acknowledge receipt of a message
static void out_failed_handler(DictionaryIterator *failed, AppMessageResult reason, void *context) {
}

void update(void){
  //get new
  incomingText = (char*) malloc(500 * sizeof(char));
  dispText = (char*) malloc(500 * sizeof(char));
  transformText = (char*) malloc(500 * sizeof(char));

  strcpy(incomingText, "new text!");
  //incomingText = "new text!"; //getFromAndroid()
  
  //cat new onto current
  // appends incoming text onto disptext
  transformText = strcat(dispText, incomingText);
  
  //cut it up and reverse it
  for (int i = 0; i < 60; i++) {
    dispText[i] = transformText[i]; 
  }
  
  
  //display it
  text_layer_set_text(text_layer, dispText);
  //printf("%s\n%s\n%s\n", incomingText, dispText, transformText);

}

static void up_click_handler(ClickRecognizerRef recognizer, void *context) {
  //print_string("Second Line \n Third Line \n Fourth Line \n Fifth Line \n Sixth Line");
}

static void down_click_handler(ClickRecognizerRef recognizer, void *context) {
  text_layer_set_text(text_layer, "First Line\n Second Line \n Third Line \n Fourth Line \n Fifth Line");
}

static void click_config_provider(void *context) {
  //window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
  window_single_click_subscribe(BUTTON_ID_UP, up_click_handler);
  window_single_click_subscribe(BUTTON_ID_DOWN, down_click_handler);
}

//void set_init_text(){
  //print_string(dispText);
//}

void handle_init() {
	// Create a window and text layer
  window = window_create();
  window_set_click_config_provider(window, click_config_provider);
	text_layer = text_layer_create(GRect(0, 0, 144, 154));
	
  app_message_register_inbox_received(in_received_handler);
	app_message_register_inbox_dropped(in_dropped_handler);
	app_message_register_outbox_failed(out_failed_handler);
  
	//set_init_text();
  update();
  text_layer_set_font(text_layer, fonts_get_system_font(FONT_KEY_GOTHIC_28_BOLD));
	text_layer_set_text_alignment(text_layer, GTextAlignmentCenter);
	
	// Add the text layer to the window
	layer_add_child(window_get_root_layer(window), text_layer_get_layer(text_layer));

	// Push the window
	window_stack_push(window, true);
	
	// App Logging!
	APP_LOG(APP_LOG_LEVEL_DEBUG, "Just pushed a window!");
}

void handle_deinit(void) {
	// Destroy the text layer
	text_layer_destroy(text_layer);
	app_message_deregister_callbacks();
	// Destroy the window
	window_destroy(window);
}

int main(void) {
	handle_init();
	app_event_loop();
	handle_deinit();
  update();
}
