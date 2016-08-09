package com.pdmanager.core.models;


/**
 * Created by george on 30/11/2015.
 */
public class Alert extends PDEntity {


    public String Title;
    public String Message;
    public String Code;
    public long Expiration;
    public long Timestamp;
    private String PatientIdentifier = "PAT01";


    public Alert(String ptitle, String pmessage, String pcode, long t, String patient) {


        this.Title = ptitle;
        this.Message = pmessage;
        this.Timestamp = t;
        this.Code = pcode;
        this.Expiration = 0;

        this.PatientIdentifier = patient;

    }

    /*  private void SetPatientIdentifier()
      {

          RecordingSettings settings= RecordingSettingsHandler.getInstance().getSettings();
          if(settings!=null)
          {

              PatientId=settings.getOrganization()+"_"+settings.getPatientID();

          }

      }
      */
    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long mTimestamp) {
        this.Timestamp = mTimestamp;
    }

    public String getTitle() {
        return Title;
    }

    public void setText(String mText) {
        this.Title = mText;
    }

    @Override
    public String getPDType() {
        return "Alert";
    }
}
