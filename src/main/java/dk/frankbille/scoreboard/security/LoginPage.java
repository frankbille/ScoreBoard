package dk.frankbille.scoreboard.security;

import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.cookies.CookieDefaults;
import org.apache.wicket.util.cookies.CookieUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.ScoreBoardApplication;
import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.components.menu.MenuItemType;
import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class LoginPage extends BasePage {
	private static final long serialVersionUID = 1L;

	static class LoginUser extends User {
		private static final long serialVersionUID = 1L;

		private boolean loginPersistent;
		private String password;

		public boolean isLoginPersistent() {
			return loginPersistent;
		}

		public void setLoginPersistent(boolean loginPersistent) {
			this.loginPersistent = loginPersistent;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	static class CreateUser extends User {
		private static final long serialVersionUID = 1L;

		private String repeatPassword;
		private String password;

		public String getRepeatPassword() {
			return repeatPassword;
		}

		public void setRepeatPassword(String repeatPassword) {
			this.repeatPassword = repeatPassword;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public LoginPage() {
		addLogin();

		addCreateUser();
	}

	private void addLogin() {
		final LoginUser user = new LoginUser();
		final Form<Void> loginForm = new Form<Void>("loginForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				if (user.isLoginPersistent()) {
					CookieDefaults settings = new CookieDefaults();
					// 14 days
					settings.setMaxAge(60*60*24*14);
					CookieUtils cookieUtils = new CookieUtils(settings);
					cookieUtils.save("loginUsername", user.getUsername());
					cookieUtils.save("loginPassword", user.getPassword());
				}
				authenticated();
			}
		};
		add(loginForm);

		loginForm.add(new FeedbackPanel("loginMessages", new ContainerFeedbackMessageFilter(loginForm)));

		final TextField<String> usernameField = new TextField<String>("usernameField", new PropertyModel<String>(user, "username"));
		loginForm.add(usernameField);

		final PasswordTextField passwordField = new PasswordTextField("passwordField", new PropertyModel<String>(user, "password"));
		loginForm.add(passwordField);

		CheckBox persistentField = new CheckBox("persistentField", new PropertyModel<Boolean>(user, "loginPersistent"));
		loginForm.add(persistentField);


		loginForm.add(new AbstractFormValidator() {
			private static final long serialVersionUID = 1L;

			@Override
			public FormComponent<?>[] getDependentFormComponents() {
				return new FormComponent<?>[] {
					usernameField,
					passwordField
				};
			}

			@Override
			public void validate(Form<?> form) {
				boolean authenticated = ScoreBoardSession.get().authenticate(usernameField.getInput(), passwordField.getInput());
				if (false == authenticated) {
					error(usernameField, "incorrectUsernamePassword");
				}
			}
		});
	}

	private void addCreateUser() {
		final CreateUser user = new CreateUser();
		final Form<Void> createUserForm = new Form<Void>("createUserForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				scoreBoardService.createUser(user, user.getPassword());
				ScoreBoardSession.get().authenticate(user.getUsername(), user.getPassword());
				authenticated();
			}
		};
		add(createUserForm);

		createUserForm.add(new FeedbackPanel("createUserMessages", new ContainerFeedbackMessageFilter(createUserForm)));

		final TextField<String> usernameField = new TextField<String>("usernameField", new PropertyModel<String>(user, "username"));
		usernameField.add(new IValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable<String> validatable) {
				if (scoreBoardService.hasUserWithUsername(validatable.getValue())) {
					ValidationError error = new ValidationError();
					error.addMessageKey("duplicateUsername");
					validatable.error(error);
				}
			}
		});
		usernameField.setRequired(true);
		createUserForm.add(usernameField);

		final PasswordTextField passwordField = new PasswordTextField("passwordField", new PropertyModel<String>(user, "password"));
		passwordField.setRequired(true);
		createUserForm.add(passwordField);

		final PasswordTextField repeatPasswordField = new PasswordTextField("repeatPasswordField", new PropertyModel<String>(user, "repeatPassword"));
		createUserForm.add(repeatPasswordField);

		createUserForm.add(new EqualPasswordInputValidator(passwordField, repeatPasswordField));
	}

	@Override
	public MenuItemType getMenuItemType() {
		return MenuItemType.SECURE;
	}

	private void authenticated() {
		if (ScoreBoardSession.get().isAuthenticated()) {
			if (false == continueToOriginalDestination()) {
				getRequestCycle().setResponsePage(ScoreBoardApplication.get().getHomePage());
			}
		}
	}

}
