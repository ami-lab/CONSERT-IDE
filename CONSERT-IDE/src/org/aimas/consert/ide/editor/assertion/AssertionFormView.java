package org.aimas.consert.ide.editor.assertion;

import java.util.List;

import org.aimas.consert.ide.editor.EditorInputWrapper;
import org.aimas.consert.ide.editor.MultiPageEditor;
import org.aimas.consert.ide.model.ContextAssertionModel;
import org.aimas.consert.ide.model.ContextEntityModel;
import org.aimas.consert.ide.model.ProjectModel;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class AssertionFormView extends FormPage implements IResourceChangeListener {
	private MultiPageEditor editor;
	private ScrolledForm form;
	private boolean isDirty;
	private ContextAssertionModel cam;
	private ProjectModel projectModel;
	public static final String ID = "org.aimas.consert.ide.editor.assertion.AssertionFormView";

	public AssertionFormView(MultiPageEditor editor) {
		super(editor, ID, "EntityFormView");
		this.editor = editor;
		isDirty = false;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public void createLabelAndText(String labelName, String textName) {
		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(labelName);
		Text nameText = new Text(form.getBody(), SWT.BORDER | SWT.SINGLE);
		nameText.setText(textName);
		nameText.setLayoutData(new GridData(100, 10));
		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				isDirty = true;
				firePropertyChange(IEditorPart.PROP_DIRTY);
				editor.editorDirtyStateChanged();

				if (labelName.equals(" Name: ")) {
					projectModel.getAssertionByName(cam.getName()).setName(nameText.getText());
				} else if (labelName.equals(" Comment: ")) {
					projectModel.getAssertionByName(cam.getName()).setComment(nameText.getText());
				} else if (labelName.equals(" Arity: ")) {
					try {
						projectModel.getAssertionByName(cam.getName()).setArity(Integer.parseInt((nameText.getText())));
					} catch (NumberFormatException exp) {
						System.err.print("Please Introduce an Integer Arity");
					}
				}
			}
		});
	}

	public void createLabelAndTextForEntity(String labelName, String textName, ContextEntityModel cem) {
		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(labelName);
		Text nameText = new Text(form.getBody(), SWT.BORDER | SWT.SINGLE);
		if (textName == null) {
			textName = "";
		}
		nameText.setText(textName);
		nameText.setLayoutData(new GridData(100, 10));
		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				isDirty = true;
				firePropertyChange(IEditorPart.PROP_DIRTY);
				editor.editorDirtyStateChanged();

				/*
				 * This entity belongs to an assertion, and is not present in
				 * the getEntities() of the ProjectModel!!!
				 */
				List<ContextEntityModel> entities = projectModel.getAssertionByName(cam.getName()).getEntities();
				for (ContextEntityModel entity : entities) {
					if (entity.equals(cem)) {
						if (labelName.equals(" Name: ")) {
							entity.setName(nameText.getText());
						} else if (labelName.equals(" Comment: ")) {
							entity.setComment(nameText.getText());
						}
					}
				}
			}
		});
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		projectModel.updateAssertionsJsonNode();
		projectModel.writeJsonOnDisk();
		isDirty = false;
		firePropertyChange(PROP_DIRTY);
		editor.editorDirtyStateChanged();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();

		EditorInputWrapper eiw = (EditorInputWrapper) getEditorInput();
		cam = (ContextAssertionModel) eiw.getModel();
		projectModel = eiw.getProjectModel();
		form.setText(cam.getName());
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		layout.numColumns = 2;

		System.out.println("Assertion inside Assertion Form View parsed: " + cam.toString());

		Label nameLabel = new Label(form.getBody(), SWT.NONE);
		nameLabel.setText(" ContextAssertion: ");
		new Label(form.getBody(), SWT.NONE);

		createLabelAndText(" Name: ", cam.getName());
		createLabelAndText(" Comment: ", cam.getComment());
		createLabelAndText(" Arity: ", Integer.toString(cam.getArity()));

		Label entitiesNameLabel = new Label(form.getBody(), SWT.NONE);
		entitiesNameLabel.setText(" ContextEntities: ");
		new Label(form.getBody(), SWT.NONE);

		for (ContextEntityModel entity : cam.getEntities()) {
			createLabelAndTextForEntity(" Name: ", entity.getName(), entity);
			createLabelAndTextForEntity(" Comment: ", entity.getComment(), entity);
		}
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		// IResourceDelta rootDelta = event.getDelta();
		// IResourceDelta affected[] = rootDelta.getAffectedChildren();
		// for (int i = 0; i < affected.length; i++) {
		// System.out.println(affected[i].getResource().getName());
		// projectName = affected[i].getResource().getName();
		// }
		// projectModel =
		// WorkspaceModel.getInstance().getProjectModel(projectName);
		System.out.println("Reload AssertionformView");
	}
}
