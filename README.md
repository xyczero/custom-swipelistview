##Android-CustomSwipeListView

Android-CustomSwipeListView that inherited from Listview and BaseAdapter supports for swiping item from right or left .

##Feature

>1.Support the custom menu in the menu which is been shown by scrolling item to left.

>2.Support delete item from the listview by scrolling item to right.

>3.Support undo the event that is deleting item .

>4.Support set the animation execution time that is been executed by swiping item from right or left.

>5.Support set the delta which determines whether to swipe item from right or left .

>6.Support set the flag which determines whether to enable to swipe item from right or left .

>7.Easy to integrate to your app.

##Gradle Dependency
```
compile 'com.xyczero:customswipelistview:1.0.0@aar'
```
##Maven Dependency
```
<dependency>
    <groupId>com.xyczero</groupId>
    <artifactId>customswipelistview</artifactId>
    <version>1.0.0</version>
    <type>aar</type>
</dependency>
```
##ScreenShot

![screenshot](http://7u2jsw.com1.z0.glb.clouddn.com/githubCustomSwipeListview_Set.PNG)

##Usage

 It is very similar to the normal use of the ListView and BaseAdapter.
 
 Firstly,you need to inherite the CustomSwipeBaseAdapter for your own adapter. The CustomSwipeListview can use it directly.
```java
 public class TestActivity extends Activity {
    private CustomSwipeListView mTestListView;
    private TestAdapter mTestAdapter;
    ... ...
 }
     
 public class TestAdapter extends CustomSwipeBaseAdapter<TestModel> {
    ... ...
 }
```
 Secondly, you need to implement some abstract function and interface that is in CustomSwipeListview and CustomSwipeBaseAdapter.
```java
 //Bind an existing item view to the data pointed to by position.
 @Override
 public void bindItemView(View view, Context context, int position) {
	    TextView textViewTitle = (TextView) view.findViewById(R.id.test_title);
		   textViewTitle.setText(getAdapterData().get(position).getTestTitle());
	... ...
 }
 
 //Bind an existing swipe left view to the data pointed to by position.
 @Override
 public void bindSwipeLeftView(View view, final Context context,final int position) {
     Button buttonFb = (Button) view.findViewById(R.id.test_fb_btn);
     buttonFb.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View v) {
				     Toast.makeText(context,"...",Toast.LENGTH_SHORT).show();
			  }
		   });
     ... ...
 }
 
 //Makes a new item view to hold the data pointed to by position.
 @Override
 public View newItemView(Context context, int position, ViewGroup parent) {
		   final View mView = mLayout.inflate(R.layout.test_listview_item_view, parent, false);
		   return mView;
 }
 
 //Makes a new swipe left view hold the data pointed to by position.
 @Override
 public View newSwipeLeftView(Context context, int position, ViewGroup parent) {
		   final View mView = mLayout.inflate(R.layout.test_listview_swipe_view, parent, false);
		   return mView;
 }
 ```
 Then you can set some properties for what you want in the activity.
 ```java
 public class TestActivity extends Activity implements RemoveItemCustomSwipeListener {
     private CustomSwipeListView mTestListView;
     private TestAdapter mTestAdapter;
     ... ...
     @Override
     protected void onCreate(Bundle savedInstanceState) {
        ... ...
        //set remove item listener
        mTestListView.setRemoveItemCustomSwipeListener(this);
        //set whether enable to swipe
        mTestListView.setSwipeItemLeftEnable(true);
        mTestListView.setSwipeItemRightEnable(true);
        //set animation execution time in millisecond
        mTestListView.setAnimationLeftDuration(300);
        mTestListView.setAnimationRightDuration(300);
        //set deltaX that triggers swiping item in dip
        mTestListView.setSwipeItemLeftTriggerDeltaX(50);
        mTestListView.setSwipeItemRightTriggerDeltaX(50);
        ... ...
    }
	    
     @Override
     public void onRemoveItemListener(int selectedPostion) {
        TestModel model = mTestAdapter.removeItemByPosition(selectedPostion);
        //set undoDialog message to show
        mUndoDialog.setMessage("Delete" + model.getTestTitle() + ".").showUndoDialog();
     }
	... ...
 }
 ```
 If you need to undo the deleting item,you just some steps.
 ```java
  public class TestActivity extends Activity implements RemoveItemCustomSwipeListener {
     private TestAdapter mTestAdapter;
     private CustomSwipeUndoDialog mUndoDialog;
     ... ...
     @Override
     protected void onCreate(Bundle savedInstanceState) {
        ... ...
        mTestAdapter = new TestAdapter(this, mTestModels);
        //UndoDialog Constructor
		      mUndoDialog = new CustomSwipeUndoDialog(this);
		      //set UndoActionListener,the listener is been implemented in CustomSwipeBaseAdapter.
		      mUndoDialog.setUndoActionListener(mTestAdapter);
		      ... ...
    }
	... ...
 }
 ```
 
 For more detatls,please see the demo.Thank you.
 
##Limitations

>1.You must ensure that add or set data to the adapter by using addAdapterData(...) and setAdapterData(...) in CustomSwipeBaseAdapter.
 
>2.You must ensure that the height of the CustemListview is a certain value(like wrap_content is forbidden).
 
##Demo Download

[Download here](http://7u2jsw.com1.z0.glb.clouddn.com/githubCustomSwipeListView.apk)

##Todo List

>1.add transition effect when swping left the item

>~~2.push to Maven and Gradle~~

##License

 Apache 2

##Contact and Help

Please contact me if there is any problem when using the library.

Email: xyczero@sina.com

Blog:  http://www.xyczero.com

Wechat: xyczero
