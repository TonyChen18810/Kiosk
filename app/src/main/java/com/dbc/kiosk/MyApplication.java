package com.dbc.kiosk;

import android.app.Application;
import android.content.Context;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.data.StringFormat;

@AcraCore(
        buildConfigClass = org.acra.BuildConfig.class,
        logcatArguments = {"-t", "200", "-v", "time"},
        reportFormat= StringFormat.JSON,
        reportContent = {
                ReportField.USER_COMMENT,
                ReportField.APP_VERSION_NAME,
                ReportField.APP_VERSION_CODE,
                ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,
                ReportField.CUSTOM_DATA,
                ReportField.STACK_TRACE,
                ReportField.LOGCAT }
)
@AcraMailSender(mailTo = "kyle.gilbert1622@gmail.com")
public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);
        // builder.setBuildConfigClass(com.dbc.kiosk.BuildConfig.class).setReportFormat(StringFormat.JSON);
        // builder.getPluginConfigurationBuilder(MailSenderConfigurationBuilder.class).setMailTo("kyle.gilbert1622@gmail.com").setEnabled(true);
        // ACRA.init(this, builder);
    }
}
