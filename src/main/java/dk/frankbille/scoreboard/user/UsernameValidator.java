/*
 * ScoreBoard
 * Copyright (C) 2012-2013 Frank Bille
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
			validatable.error(new ValidationError().addKey("mustSpecifyAUsername"));
			return;
		}
		
		if (username.length() <= 2) {
			validatable.error(new ValidationError().addKey("usernameMustBeAtleastThreeCharacters"));
			return;
		} 
		
		User user = userModel.getObject();
		if (username.equals(user.getUsername()) == false) {
			if (scoreBoardService.hasUserWithUsername(username)) {
				validatable.error(new ValidationError().addKey("duplicateUsername"));
				return;
			}
		}
	}

}
