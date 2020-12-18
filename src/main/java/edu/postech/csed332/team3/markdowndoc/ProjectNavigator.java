package edu.postech.csed332.team3.markdowndoc;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import edu.postech.csed332.team3.markdowndoc.explorer.ActiveProjectModel;
import edu.postech.csed332.team3.markdowndoc.explorer.ProjectModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.security.InvalidParameterException;

/**
 * Class providing methods for navigating to the code location
 * when the user clicks on a link in MarkdownDoc.
 */
public class ProjectNavigator {

    private final Project project;
    private DefaultMutableTreeNode root;
    private String qualifiedClassName;
    private String elementName;
    private String elementType;
    private int overloadIndex;

    public ProjectNavigator() {
        project = ActiveProjectModel.getActiveProject();
        loadModel();
    }

    private void loadModel() {
        TreeModel model = ProjectModel.createProjectTreeModel(project);
        root = (DefaultMutableTreeNode) (model.getRoot());
    }

    // Input is of form m-method;com.example.package.Class
    private void parseInput(String input) {
        String[] part = input.split(";");
        if (part.length != 2) throw new InvalidParameterException("Invalid parameter");

        elementName = part[0].substring(2);
        elementType = part[0].substring(0, 1);
        qualifiedClassName = part[1];

        // Additional parsing for method overloading
        if (elementType.equals("m")) {
            String[] methodID = elementName.split("/");
            elementName = methodID[0];
            overloadIndex = Integer.parseInt(methodID[1]);
        }
    }

    // Find element using qualifiedClassName, elementName, elementType
    private PsiElement find() {
        // Get the class element
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(qualifiedClassName, GlobalSearchScope.projectScope(project));
        DefaultMutableTreeNode classNode = find(root, psiClass);
        if (classNode == null)
            return null;

        int overloadCounter = 1;
        PsiNamedElement element;
        for (int i = 0; i < classNode.getChildCount(); i++) {
            Object userObject = ((DefaultMutableTreeNode) classNode.getChildAt(i)).getUserObject();
            if (!(userObject instanceof PsiNamedElement))
                continue;
            element = checkName((PsiNamedElement) userObject, overloadCounter);
            if (element == null)
                overloadCounter++;
            else
                return element;
        }

        return null;
    }

    private PsiNamedElement checkName(PsiNamedElement element, int counter) {
        if (element.getName() == null)
            return null;
        if (element.getName().equals(elementName) && elementType.equals("m") && counter != overloadIndex)
            return null;
        return element;
    }

    // Find PsiElement in tree
    private DefaultMutableTreeNode find(@NotNull DefaultMutableTreeNode node, PsiElement target) {
        if (node.getUserObject() == target) {
            return node;
        }
        DefaultMutableTreeNode n = null;
        for (int i = 0; i < node.getChildCount(); i++) {
            n = find((DefaultMutableTreeNode) node.getChildAt(i), target);
            if (n != null) break;
        }
        return n;
    }

    /**
     * Navigate to the editor
     * if the clicked item is a method or a field
     *
     * @param input the input string (console output from MarkdownDoc)
     */
    public void navigateToMethodField(String input) {
        ApplicationManager.getApplication().invokeLater(() -> {
            loadModel();
            parseInput(input);
            PsiElement element = find();

            if (element instanceof PsiMethod) {
                ((PsiMethod) element).navigate(true);
            } else if (element instanceof PsiField) {
                ((PsiField) element).navigate(true);
            }
        });
    }

    /**
     * Navigate to the editor
     * if the clicked item is a class
     * and is not invoked by a link
     *
     * @param input the input string (console output from MarkdownDoc)
     */
    public void navigateToClass(String input) {
        ApplicationManager.getApplication().invokeLater(() -> {
            loadModel();
            parseInput(input);
            PsiElement element = find();

            if (element instanceof PsiClass && !elementName.contains("."))
                ((PsiClass) element).navigate(true);
        });
    }
}
