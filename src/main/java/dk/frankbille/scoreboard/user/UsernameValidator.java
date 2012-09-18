package dk.frankbille.scoreboard.user;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.service.ScoreBoardService;

/**
 * Validates a username, checking against the database if somebody already uses
 * that username.
 */
public class UsernameValidator implements IValidator<String> {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	private IModel<User> userModel;
	
	public UsernameValidator(IModel<User> userModel) {
		Injector.get().inject(this);
		this.userModel = userModel;
	}
	
	@Override
	public void validate(IValidatable<String> validatable) {
		String username = validatable.getValue();
		if (username == null || username.length() == 0) {
			validatable.error(new ValidationError().addMessageKey("mustSpecifyAUsername"));
			return;
		}
		
		if (username.length() <= 2) {
			validatable.error(new ValidationError().addMessageKey("usernameMustBeAtleastThreeCharacters"));
			return;
		} 
		
		User user = userModel.getObject();
		if (username.equals(user.getUsername()) == false) {
			if (scoreBoardService.hasUserWithUsername(username)) {
				validatable.error(new ValidationError().addMessageKey("duplicateUsername"));
				return;
			}
		}
	}

}
