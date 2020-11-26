package edu.postech.csed332.team3.markdowndoc;

import com.intellij.openapi.externalSystem.service.execution.NotSupportedException;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefJSQuery;
import edu.postech.csed332.team3.markdowndoc.SearchProject.SearchProject;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BrowserController {

    private final JBCefBrowser browser;
    private final BrowserView view;
    private final CefBrowser cefBrowser;
    private final ProjectNavigator navigator;

    private final SearchProject searchProject;

    // JSHandler for communication
    private JBCefJSQuery linkQuery;

    /**
     * Create an empty browser controller instance
     */
    public BrowserController(BrowserView view) throws IOException {
        if (!JBCefApp.isSupported()) {
            throw new NotSupportedException("This IDE version is not supported.");
        }

        navigator = new ProjectNavigator();

        // Get project root directory and load it in the browser
        @NotNull VirtualFile projectRoot = ModuleRootManager.getInstance(
                ModuleManager.getInstance(navigator.getActiveProject()).getModules()[0]
        ).getContentRoots()[0];

        browser = new JBCefBrowser("file://" + projectRoot.getCanonicalPath() + "/mdsaved");
        this.view = view;
        cefBrowser = browser.getCefBrowser();

        // Finalize the view
        view.addComponent(browser.getComponent(), "Center"); // Add browser

        setListeners();
        setHandlers();

        // Initialize SearchProject
        searchProject = new SearchProject();
        searchProject.init("./src");
        searchProject.start();
    }

    private void setListeners() {
        // Set action listeners
        view.getBackButton().addActionListener(e -> {
            goBack();
        });

        view.getForwardButton().addActionListener(e -> {
            goForward();
        });
    }

    private void setHandlers() {
        cefBrowser.getClient().addDisplayHandler(new CefDisplayHandler() {
            @Override
            public void onAddressChange(CefBrowser cefBrowser, CefFrame cefFrame, String s) {
                updateView();
            }

            @Override
            public void onTitleChange(CefBrowser cefBrowser, String s) {
                updateView();
            }

            @Override
            public boolean onTooltip(CefBrowser cefBrowser, String s) {
                return false;
            }

            @Override
            public void onStatusMessage(CefBrowser cefBrowser, String s) {

            }

            @Override
            public boolean onConsoleMessage(CefBrowser cefBrowser, CefSettings.LogSeverity logSeverity, String s, String s1, int i) {
                view.getResponseLabel().setText(s1);
                return false;
            }
        });
    }

    /**
     * Update the view (button status, etc.)
     */
    public void updateView() {
        // Change button enabled status
        view.getBackButton().setEnabled(canGoBack());
        view.getForwardButton().setEnabled(canGoForward());
    }

    /**
     * Loads URL in the browser
     *
     * @param url the URL
     */
    public void loadURL(String url) {
        browser.loadURL(url);
    }

    /**
     * Loads HTML in the browser
     *
     * @param html HTML content
     */
    public void loadHTML(String html) {
        browser.loadHTML(html);
    }

    /**
     * Return the URL
     *
     * @return the URL
     */
    public String getURL() {
        return cefBrowser.getURL();
    }

    /**
     * Go back in history if possible
     */
    public void goBack() {
        cefBrowser.goBack();
        updateView();
    }

    /**
     * Go forward in history if possible
     */
    public void goForward() {
        cefBrowser.goForward();
        updateView();
    }

    /**
     * Return whether the browser can go back in history
     *
     * @return true if browser can go back
     */
    public boolean canGoBack() {
        return cefBrowser.canGoBack();
    }

    /**
     * Return whether the browser can go forward in history
     *
     * @return true if browser can go forward
     */
    public boolean canGoForward() {
        return cefBrowser.canGoForward();
    }

    /**
     * Execute a string of JavaScript code in this frame. The url
     * parameter is the URL where the script in question can be found, if any.
     * The renderer may request this URL to show the developer the source of the
     * error. The line parameter is the base line number to use for error
     * reporting.
     *
     * @param code The code to be executed.
     */
    public void executeJavaScript(String code) {
        cefBrowser.executeJavaScript(code, getURL(), 0);
    }
}