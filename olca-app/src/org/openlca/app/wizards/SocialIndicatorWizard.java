package org.openlca.app.wizards;

import java.util.UUID;

import org.eclipse.swt.widgets.Composite;
import org.openlca.app.Messages;
import org.openlca.app.db.Database;
import org.openlca.core.database.BaseDao;
import org.openlca.core.model.SocialIndicator;

public class SocialIndicatorWizard extends AbstractWizard<SocialIndicator> {

	@Override
	protected BaseDao<SocialIndicator> createDao() {
		return Database.createDao(SocialIndicator.class);
	}

	@Override
	protected String getTitle() {
		return Messages.NewSocialIndicator;
	}

	@Override
	protected AbstractWizardPage<SocialIndicator> createPage() {
		return new Page();
	}

	private class Page extends AbstractWizardPage<SocialIndicator> {

		public Page() {
			super("SocialIndicatorWizardPage");
			setTitle(Messages.NewSocialIndicator);
			setPageComplete(false);
		}

		@Override
		protected void createContents(Composite container) {
		}

		@Override
		public SocialIndicator createModel() {
			SocialIndicator i = new SocialIndicator();
			i.setRefId(UUID.randomUUID().toString());
			i.setName(getModelName());
			i.setDescription(getModelDescription());
			return i;
		}
	}

}
