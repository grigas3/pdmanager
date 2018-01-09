package com.pdmanager.views.call;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pdmanager.R;
import com.pdmanager.app.VideoApp.CallNegotiationListener;
import com.pdmanager.app.VideoApp.MessageCompletionHandler;
import com.pdmanager.call.CNMessage;
import com.pdmanager.call.CNMessage.CNMessageType;
import com.pdmanager.views.BasePDFragment;

import java.util.ArrayList;
import java.util.List;

public class CallNegotiationFragment extends BasePDFragment implements View.OnClickListener, CallNegotiationListener {

    private static final String KEY_ADAPTER_STATE = "com.oovoo.sdk.fragmentstate.KEY_ADAPTER_STATE";
    private static final int MAX_CALL_RECEIVERS = 4;
    private CallReceiverAdapter callReceiverAdapter = null;
    private AlertDialog callDialogBuilder = null;
    private AlertDialog callReceiverDialog = null;
    private ArrayList<CallReceiverAdapter.CallReceiver> adapterSavedState = null;
    private int count = 0;
    private int enabledReceivesCount = 0;

    public CallNegotiationFragment() {
    }

    public static final CallNegotiationFragment newInstance() {
        CallNegotiationFragment fragment = new CallNegotiationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_ADAPTER_STATE)) {
            adapterSavedState = savedInstanceState.getParcelableArrayList(KEY_ADAPTER_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.call_negotiation_fragment, container, false);

        callReceiverAdapter = new CallReceiverAdapter();

        if (adapterSavedState != null) {
            callReceiverAdapter.onRestoreInstanceState(adapterSavedState);
        }

        final EditText callee = (EditText) view.findViewById(R.id.callee);

        Button callButton = (Button) view.findViewById(R.id.start_call_button);
        callButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                callReceiverAdapter.receivers.clear();
                callReceiverAdapter.addItem(callee.getText().toString());

                app().generateConferenceId();

                enabledReceivesCount = callReceiverAdapter.getEnabledReceivesCount();

                boolean showDialog = sendCNMessage(CNMessageType.Calling, 30000, new MessageCompletionHandler() { // Timeout 30 sec
                    @Override
                    public void onHandle(final boolean state) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!state) {
                                    count = 0;
                                    Toast.makeText(getActivity(), R.string.fail_to_send_message, Toast.LENGTH_LONG).show();
                                    callDialogBuilder.hide();
                                    return;
                                }

                                count = enabledReceivesCount;
                            }
                        });
                    }
                });

                if (showDialog) {
                    callDialogBuilder.show();
                } else {
                    Toast.makeText(getActivity(), R.string.no_receivers, Toast.LENGTH_LONG).show();
                }
            }
        });

        callDialogBuilder = new AlertDialog.Builder(getActivity()).create();

        View outgoingCallDialog = inflater.inflate(R.layout.outgoing_call_dialog, null);
        outgoingCallDialog.setAlpha(0.5f);
        callDialogBuilder.setView(outgoingCallDialog);
        Button cancelButton = (Button) outgoingCallDialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);

        callDialogBuilder.setCancelable(false);

        app().addCallNegotiationListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (callReceiverAdapter != null) {
            adapterSavedState = callReceiverAdapter.onSaveInstanceState();
        }

        outState.putParcelableArrayList(KEY_ADAPTER_STATE, adapterSavedState);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().getWindow().setBackgroundDrawableResource(R.drawable.slqsm);
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onDestroyView() {
        if (callReceiverDialog != null) {
            callReceiverDialog.getWindow().setSoftInputMode(0);
            callReceiverDialog.hide();
        }
        callDialogBuilder.hide();
        app().removeCallNegotiationListener(this);

        if (callReceiverAdapter != null) {
            adapterSavedState = callReceiverAdapter.onSaveInstanceState();
        }

        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {

        callDialogBuilder.hide();

        switch (v.getId()) {
            case R.id.cancel_button: {
                sendCNMessage(CNMessageType.Cancel, null);
            }
            break;

            default:
                break;
        }
    }

    private boolean sendCNMessage(CNMessageType type, long timeout, MessageCompletionHandler completionHandler) {
        ArrayList<String> toList = new ArrayList<String>();
        for (int i = 0; i < callReceiverAdapter.getCount(); i++) {
            CallReceiverAdapter.CallReceiver receiver = callReceiverAdapter.getItem(i);

            if (receiver.isCallEnabled() && toList.size() < MAX_CALL_RECEIVERS) {
                toList.add(receiver.getReceiverId());
            }
        }

        if (toList.isEmpty()) {
            return false;
        }

        app().sendCNMessage(toList, type, timeout, completionHandler);

        return true;
    }

    private boolean sendCNMessage(CNMessageType type, MessageCompletionHandler completionHandler) {
        return sendCNMessage(type, 0, completionHandler);
    }

    @Override
    public void onMessageReceived(CNMessage cnMessage) {
        if (app().getUniqueId().equals(cnMessage.getUniqueId())) {
            return;
        }

        if (cnMessage.getMessageType() == CNMessage.CNMessageType.AnswerAccept) {
            app().join(app().getConferenceId(), true);
        } else if (cnMessage.getMessageType() == CNMessage.CNMessageType.AnswerDecline) {
            count--;
            if (count <= 0) {
                callDialogBuilder.hide();
            }
        } else if (cnMessage.getMessageType() == CNMessageType.Busy) {
            count--;
            if (count <= 0) {
                Toast.makeText(getActivity(), R.string.call_busy, Toast.LENGTH_SHORT).show();
                callDialogBuilder.hide();
            }
        }
    }

    public BasePDFragment getBackFragment() {
        return CallNegotiationFragment.newInstance();
    }

    private static class CallReceiverAdapter extends BaseAdapter {
        private final List<CallReceiver> receivers = new ArrayList<CallReceiver>();

        ArrayList<CallReceiver> onSaveInstanceState() {
            int size = getCount();
            ArrayList<CallReceiver> items = new ArrayList<CallReceiver>(size);
            for (int i = 0; i < size; i++) {
                items.add(getItem(i));
            }
            return items;
        }

        void onRestoreInstanceState(ArrayList<CallReceiver> items) {
            receivers.clear();
            receivers.addAll(items);
        }

        public int getEnabledReceivesCount() {
            int count = 0;
            for (int i = 0; i < receivers.size(); i++) {
                CallReceiverAdapter.CallReceiver receiver = getItem(i);
                if (receiver.isCallEnabled() && count < MAX_CALL_RECEIVERS) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public int getCount() {
            return receivers.size();
        }

        @Override
        public CallReceiver getItem(int i) {
            return receivers.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i; // index number
        }

        @Override
        public View getView(int index, View view, final ViewGroup parent) {
            return view;
        }

        public void addItem(String receiverId) {
            if (receiverId == null || receiverId.isEmpty())
                return;

            receivers.add(new CallReceiver(receiverId));
        }

        public static class CallReceiver implements Parcelable {
            @SuppressWarnings("unused")
            public static final Parcelable.Creator<CallReceiver> CREATOR = new Parcelable.Creator<CallReceiver>() {
                @Override
                public CallReceiver createFromParcel(Parcel in) {
                    return new CallReceiver(in);
                }

                @Override
                public CallReceiver[] newArray(int size) {
                    return new CallReceiver[size];
                }
            };
            private String receiverId = null;
            private Boolean isCallEnabled = true;

            public CallReceiver(String receiverId) {
                this.receiverId = receiverId;
            }

            protected CallReceiver(Parcel in) {
                receiverId = in.readString();
                isCallEnabled = in.readByte() != 0;
            }

            public String getReceiverId() {
                return receiverId;
            }

            public boolean isCallEnabled() {
                return this.isCallEnabled;
            }

            public void setCallEnabled(boolean isCallEnabled) {
                this.isCallEnabled = isCallEnabled;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(receiverId);
                dest.writeByte((byte) (isCallEnabled ? 1 : 0));
            }
        }
    }
}
