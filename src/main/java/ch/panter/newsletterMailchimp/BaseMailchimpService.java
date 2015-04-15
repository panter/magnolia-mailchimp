package ch.panter.newsletterMailchimp;

import info.magnolia.newsletter.SubscriptionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.MailChimpMethod;
import com.ecwid.mailchimp.MailChimpObject;
import com.ecwid.mailchimp.method.v2_0.lists.Email;
import com.ecwid.mailchimp.method.v2_0.lists.ListMethod;
import com.ecwid.mailchimp.method.v2_0.lists.ListMethodResult;
import com.ecwid.mailchimp.method.v2_0.lists.ListMethodResult.Data;
import com.ecwid.mailchimp.method.v2_0.lists.SubscribeMethod;
import com.ecwid.mailchimp.method.v2_0.lists.UnsubscribeMethod;

public abstract class BaseMailchimpService implements SubscriptionService {

	private static final int List_AlreadySubscribed = 214;
	private String apiKey;
	private String defaultListId;

	private MailChimpClient mailChimpClient = new MailChimpClient();
	private Boolean doubleOptin;
	private Boolean updateExisting;

	public void subscribe(String email, String displayName) {
		subscribe(email, displayName, getDefaultListId());

	}

	public void subscribe(String email, String displayName, String listId) {
		subscribe(email, displayName, listId, null);
	}

	public void subscribe(String email, String displayName,
			Map<String, String> customFields) {
		subscribe(email, displayName, getDefaultListId(), customFields);

	}

	public void subscribe(String email, String displayName, String listId,
			Map<String, String> customFields) {
		subscribe(email, displayName, listId, customFields, updateExisting);
	}

	public void subscribe(String email, String displayName, String listId,
			Map<String, String> customFields, boolean updateExisting) {

		SubscribeMethod subscribeMethod = new SubscribeMethod();
		subscribeMethod.apikey = getApiKey();
		if (listId != null)
			subscribeMethod.id = listId;
		else
			subscribeMethod.id = getDefaultListId();

		subscribeMethod.email = new Email();
		subscribeMethod.email.email = email;
		/**
		 * TODO: define these values through config
		 */
		subscribeMethod.update_existing = updateExisting;
		subscribeMethod.double_optin = doubleOptin;

		subscribeMethod.merge_vars = createMergeVars(email, displayName,
				customFields);
		executeMethod(subscribeMethod);
	}

	/**
	 * Holds a subscriber's merge_vars info (see
	 * http://apidocs.mailchimp.com/api/2.0/lists/subscribe.php )
	 * 
	 * Typically, this is project-dependent, so implemenet your own in your
	 * project.
	 * 
	 * @param email
	 * @param displayName
	 * @param customFields
	 * @return
	 */
	protected abstract MailChimpObject createMergeVars(String email,
			String displayName, Map<String, String> customFields);

	public List<List<String>> getClientLists() {
		List<List<String>> outList = new ArrayList<List<String>>();
		ListMethod listMethod = new ListMethod();
		listMethod.apikey = getApiKey();
		ListMethodResult result;

		result = executeMethod(listMethod);

		List<Data> inList = result.data;

		for (Data data : inList) {
			List<String> row = new ArrayList<String>();
			row.add(data.id);
			row.add(data.name);
			outList.add(row);
		}

		return outList;
	}

	private <Result> Result executeMethod(MailChimpMethod<Result> listMethod) {
		Result result = null;
		try {
			result = mailChimpClient.execute(listMethod);
		} catch (IOException e) {
			throw new IllegalStateException("IOException", e);
		} catch (MailChimpException e) {
			switch (e.code) {
			case List_AlreadySubscribed:
				throw new UserAlreadyExistsException("User already exists", e);
			default:
				throw new RuntimeException("MailChimpException", e);
			}

		}
		return result;
	}

	public void unsubscribe(String email) {
		unsubscribe(email, null);
	}

	public void unsubscribe(String email, String listId) {
		UnsubscribeMethod unsubscribeMethod = new UnsubscribeMethod();
		unsubscribeMethod.apikey = getApiKey();
		unsubscribeMethod.email = new Email();
		unsubscribeMethod.email.email = email;
		unsubscribeMethod.id = listId;
		executeMethod(unsubscribeMethod);

	}

	public Boolean getDoubleOptin() {
		return doubleOptin;
	}

	public void setDoubleOptin(Boolean doubleOptin) {
		this.doubleOptin = doubleOptin;
	}

	public Boolean getUpdateExisting() {
		return updateExisting;
	}

	public void setUpdateExisting(Boolean updateExisting) {
		this.updateExisting = updateExisting;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getDefaultListId() {
		return defaultListId;
	}

	public void setDefaultListId(String defaultListId) {
		this.defaultListId = defaultListId;
	}

	public MailChimpClient getMailChimpClient() {
		return mailChimpClient;
	}

}
