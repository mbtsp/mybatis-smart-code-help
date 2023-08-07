package com.mybatis.model;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.util.ui.AbstractTableCellEditor;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

public class MyAutoCompletionWithBrowserTableCellEditor extends AbstractTableCellEditor implements Disposable {
    private final TextFieldWithAutoCompletion<String> textFieldWithAutoCompletion;

    public MyAutoCompletionWithBrowserTableCellEditor(Project project) {
        textFieldWithAutoCompletion = TextFieldWithAutoCompletion.create(project, Collections.emptyList(), true, null);
    }

    public MyAutoCompletionWithBrowserTableCellEditor(Project project, List<String> items) {
        textFieldWithAutoCompletion = TextFieldWithAutoCompletion.create(project, items, true, null);
    }

    /**
     * Usually not invoked directly, see class javadoc.
     */
    @Override
    public void dispose() {
        textFieldWithAutoCompletion.removeNotify();
    }

    /**
     * Sets an initial <code>value</code> for the editor.  This will cause
     * the editor to <code>stopEditing</code> and lose any partially
     * edited value if the editor is editing when this method is called. <p>
     * <p>
     * Returns the component that should be added to the client's
     * <code>Component</code> hierarchy.  Once installed in the client's
     * hierarchy this component will then be able to draw and receive
     * user input.
     *
     * @param table      the <code>JTable</code> that is asking the
     *                   editor to edit; can be <code>null</code>
     * @param value      the value of the cell to be edited; it is
     *                   up to the specific editor to interpret
     *                   and draw the value.  For example, if value is
     *                   the string "true", it could be rendered as a
     *                   string or it could be rendered as a check
     *                   box that is checked.  <code>null</code>
     *                   is a valid value
     * @param isSelected true if the cell is to be rendered with
     *                   highlighting
     * @param row        the row of the cell being edited
     * @param column     the column of the cell being edited
     * @return the component for editing
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textFieldWithAutoCompletion.setText((String) value);
        return this.textFieldWithAutoCompletion;
    }

    /**
     * Returns the value contained in the editor.
     *
     * @return the value contained in the editor
     */
    @Override
    public Object getCellEditorValue() {
        return this.textFieldWithAutoCompletion.getText();
    }

    /**
     * Returns true.
     *
     * @param e an event object
     * @return true
     */
    @Override
    public boolean isCellEditable(EventObject e) {
        return super.isCellEditable(e);
    }
}
