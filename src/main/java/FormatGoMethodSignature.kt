import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler

/**
 * @author izumix03
 */
class FormatGoMethodSignature @JvmOverloads constructor(setupHandler: Boolean = true) : EditorAction(null) {
    init {
        if (setupHandler) {
            this.setupHandler(object : EditorWriteActionHandler(true) {
                override fun executeWriteAction(editor: Editor, caret: Caret?, dataContext: DataContext) {
                    if (editor.isColumnMode) {
                        return
                    }

                    val selectionModel = editor.selectionModel
                    if (!selectionModel.hasSelection()) {
                        selectionModel.selectLineAtCaret()
                    }
                    val selectedText = selectionModel.selectedText ?: return

                    val newText = processSelection(selectedText)
                    applyChanges(editor, selectionModel, newText)
                }
            })
        }
    }

    private fun processSelection(selectedText: String): String =
            selectedText.replace("\\((?!\\n)".toRegex(), "(\n\t")
                    .replace("(?<!\\n)\\)".toRegex(), ",\n)")
                    .replace(",(?!\\n)".toRegex(), ",\n\t")

    private fun applyChanges(editor: Editor, selectionModel: SelectionModel, newText: String) =
            editor.document.replaceString(
                    selectionModel.selectionStart,
                    selectionModel.selectionEnd,
                    newText)
}