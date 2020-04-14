package xyz.acrylicstyle.hackReport;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.acrylicstyle.hackReport.gui.ReportConfirmGui;
import xyz.acrylicstyle.hackReport.gui.ReportGui;
import xyz.acrylicstyle.tomeito_core.utils.Log;

public class HackReport extends JavaPlugin {
    public static final ReportGui REPORT_GUI = new ReportGui();
    public static final ReportConfirmGui REPORT_CONFIRM_GUI = new ReportConfirmGui();

    @Override
    public void onEnable() {
        Log.info("Enabled HackReport");
    }
}
