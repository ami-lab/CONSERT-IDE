package org.aimas.consert.ide.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.aimas.consert.ide.model.ProjectModel;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

public class EditorInputWrapper implements IStorageEditorInput {
	private Object model;
	private StringStorage storage;
	private ProjectModel projectModel;

	public ProjectModel getProjectModel() {
		return projectModel;
	}

	public void setProjectModel(ProjectModel projectModel) {
		this.projectModel = projectModel;
	}

	public EditorInputWrapper(Object model) {
		this.model = model;
		storage = new StringStorage();
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	@Override
	public IStorage getStorage() throws CoreException {
		return storage;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Displays a EditorInputWrapper";
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EditorInputWrapper other = (EditorInputWrapper) obj;
		if (model != other.model)
			return false;
		return true;
	}

	class StringStorage implements IStorage {

		@Override
		public InputStream getContents() throws CoreException {
			return new ByteArrayInputStream(model.toString().getBytes(StandardCharsets.UTF_8));
		}

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			return null;
		}

		@Override
		public IPath getFullPath() {
			return null;
		}

		@Override
		public String getName() {
			return this.getClass().getName();
		}

		@Override
		public boolean isReadOnly() {
			/*
			 * This must be false, otherwise the editor can't edit the text
			 * inside.
			 */
			return false;
		}
	}
}
