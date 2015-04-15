package ch.panter.newsletterMailchimp;

import java.util.Map;

import com.ecwid.mailchimp.MailChimpObject;

/**
 * a Sample class that creates Merge Vars with only the display name
 * Typically, this is project-dependent, so implmenet your own in your project
 * @author macrozone
 *
 */
public class DefaultMailchimpService extends BaseMailchimpService {

	protected MailChimpObject createMergeVars(String email, String realName,
			Map<String, String> customFields) {
		return new DefaultMergeVars(email, realName);
	}

	// Holds a subscriber's merge_vars info (see
	// http://apidocs.mailchimp.com/api/2.0/lists/subscribe.php )
	public static class DefaultMergeVars extends MailChimpObject {

		private static final long serialVersionUID = 1L;
		@Field
		public String EMAIL, DNAME;

		public DefaultMergeVars() {
		}

		public DefaultMergeVars(String email, String displayName) {
			this.EMAIL = email;
			this.DNAME = displayName;

		}
	}

}
