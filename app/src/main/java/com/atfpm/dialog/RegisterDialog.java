package com.atfpm.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atfpm.R;
import com.atfpm.main.user.ForgetActivityV2;
import com.atfpm.main.user.PrivacidadeActivity;
import com.atfpm.main.user.RegisterActivityV2;
import com.atfpm.system.SystemHandle;
import com.atfpm.tool.Passageway;
import com.atfpm.tool.WinTool;

/**
 * *
 * * ┏┓      ┏┓
 * *┏┛┻━━━━━━┛┻┓
 * *┃          ┃
 * *┃          ┃
 * *┃ ┳┛   ┗┳  ┃
 * *┃          ┃
 * *┃    ┻     ┃
 * *┃          ┃
 * *┗━┓      ┏━┛
 * *  ┃      ┃
 * *  ┃      ┃
 * *  ┃      ┗━━━┓
 * *  ┃          ┣┓
 * *  ┃         ┏┛
 * *  ┗┓┓┏━━━┳┓┏┛
 * *   ┃┫┫   ┃┫┫
 * *   ┗┻┛   ┗┻┛
 * Created by Hua on 15/8/17.
 */
public class RegisterDialog implements View.OnClickListener {

    public final static String KEY = "REGISTER_KEY";

    public final static int COM_MAIN = 1;
    public final static int COM_LGOIN = 2;

    private final static String CONTENT = "嚴禁於本應用程式發佈以下內容：\n" +
            "1.違反法律的言論及圖片。\n" +
            "2.誹謗、恐嚇、侮辱他人的言論及圖片。\n" +
            "3.宣揚暴力、迷信、色情淫穢及散播謠言的言論及圖片。\n" +
            "任何會員一經發現在本應用程式內發表上述言論的，將被永久性取消會員資格，並由會員單獨承擔其所發表言論的責任。\n" +
            "\n" +
            "É rigorosamente proibido publicar nesse aplicação o seguinte :\n" +
            "1.Expressões e imagens que violam as legislações em vigor.\n" +
            "2.Expressões e imagens que difamam, ameaçam, insultam outras pessoas.\n" +
            "3.Expressões e imagens que promovem a violência, a superstição, a pornografia e o boato.\n" +
            "\n" +
            "Qualquer membro que viola as regras acima referidas será permanentemente cancelado o seu direito de acesso  e as responsabilidades cabem única e exclusivamente ao subscritor das mensagens.";

    private Context context;
    private AlertDialog ad;
    private Window window;

    private TextView commit, content;
    private LinearLayout checkBox;
    private ImageView check;

    private int com;

    private boolean noExcep = true;
    private boolean isCheck = false;

    public RegisterDialog(Context context, int com) {
        this.com = com;
        this.context = context;
        try {
            ad = new AlertDialog.Builder(context).create();
            ad.show();

            window = ad.getWindow();
            window.setContentView(R.layout.speak_warning);

            content = (TextView) window.findViewById(R.id.speak_warning_content);
            commit = (TextView) window.findViewById(R.id.speak_warning_commit);
            check = (ImageView) window.findViewById(R.id.speak_warning_check);
            checkBox = (LinearLayout) window.findViewById(R.id.speak_warning_checkBox);

//            setLayout();
            setContent();
            setCheckIcon(isCheck);
            setOnClick();
            setCanceledOnTouchOutside(false);
        } catch (Exception e) {
            noExcep = false;
        }
    }

    private void setContent() {
        if (noExcep) {
            content.setText(CONTENT);
        }
    }

    private void setOnClick() {
        if (noExcep) {
            commit.setOnClickListener(this);
            checkBox.setOnClickListener(this);
        }
    }

    public void setLayout() {
        setLayout(0.9, 0.73);
    }

    public void setLayout(double Xnum, double Ynum) {
        if (noExcep) {
            window.setLayout((int) (WinTool.getWinWidth(context) * Xnum),
                    (int) (WinTool.getWinHeight(context) * Ynum));
        }
    }

    public void setCanceledOnTouchOutside(boolean isOutside) {
        if (noExcep) {
            ad.setCanceledOnTouchOutside(isOutside);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.speak_warning_checkBox:
                isCheck = !isCheck;
                setCheckIcon(isCheck);
                break;
            case R.id.speak_warning_commit:
                saveIsChrak();
                Passageway.jumpActivity(context, RegisterActivityV2.class);
                dismiss();
                break;
        }
    }

    private void saveIsChrak() {
        SystemHandle.saveBooleanMessage(context, KEY + "_" + com, isCheck);
    }


    private void dismiss() {
        ad.dismiss();
    }

    public void setCheckIcon(boolean b) {
        if (b) {
            check.setImageResource(R.drawable.speak_on_icon);
        } else {
            check.setImageResource(R.drawable.speak_off_icon);
        }
    }

    public static void showWarningDialog(Context context, int com) {
//        if (!SystemHandle.getBoolean(context, RegisterDialog.KEY + "_" + com)) {
//            RegisterDialog dialog = new RegisterDialog(context, com);
//        } else {
//            Passageway.jumpActivity(context, RegisterActivityV2.class);
//        }
        Bundle b = new Bundle();
        b.putBoolean(PrivacidadeActivity.KEY, true);
        Passageway.jumpActivity(context, PrivacidadeActivity.class, b);
    }
}
