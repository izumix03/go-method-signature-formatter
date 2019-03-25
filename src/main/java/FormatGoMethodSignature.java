import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;

/**
 * @author Olivier Smedile
 * @author Vojtech Krasa
 */
public class FormatGoMethodSignature extends EditorAction {
    public FormatGoMethodSignature(boolean setupHandler) {
        super(null);
        if (setupHandler) {
            this.setupHandler(new EditorWriteActionHandler(true) {

                @Override
                public void executeWriteAction(Editor editor, Caret caret, DataContext dataContext) {
                    if (editor.isColumnMode()) {
                        return;
                    }

                    final SelectionModel selectionModel = editor.getSelectionModel();
                    boolean hasSelection = selectionModel.hasSelection();
                    if (!hasSelection) {
                        selectionModel.selectLineAtCaret();
                    }
                    final String selectedText = selectionModel.getSelectedText();

                    if (selectedText != null) {
                        final String newText = processSelection(selectedText);
                        applyChanges(editor, selectionModel, newText);
                    }
                }
            });

        }
    }

    private String processSelection(String selectedText) {
        return selectedText.replaceAll("\\((?!\\n)", "(\n\t")
                .replaceAll("(?<!\\n)\\)", ",\n)")
                .replaceAll(",(?!\\n)", ",\n\t");
    }

    public FormatGoMethodSignature() {
        this(true);
    }


    private void applyChanges(Editor editor, SelectionModel selectionModel, String newText) {
        editor.getDocument().replaceString(
                selectionModel.getSelectionStart(),
                selectionModel.getSelectionEnd(),
                newText);
    }
}