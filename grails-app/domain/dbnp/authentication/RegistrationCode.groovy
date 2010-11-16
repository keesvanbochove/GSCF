/* Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dbnp.authentication

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class RegistrationCode {
	int userId
	String token = UUID.randomUUID().toString().replaceAll('-', '')
	Date dateCreated
	Date expiryDate

	static mapping = {
		version false
	}

	public static boolean deleteByUser( SecUser user ) {
		RegistrationCode.findByUserId(user.id)*.delete();
		return true;
	}
}
