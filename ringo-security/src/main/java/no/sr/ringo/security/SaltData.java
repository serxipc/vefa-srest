package no.sr.ringo.security;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Object holding accountId and registrationDate retrieved from db.
 * Provides method to of salt
 * @author adam
 *
 */
public class SaltData {

	final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private Integer accountId;
	private Date create_ts;

	public SaltData(final Integer accountId, final Date registrationDate) {
		this.accountId = accountId;
		this.create_ts = registrationDate;
	}

	/**
	 * Returns salt as a concatenation of originatorId and registrationDate
	 * @return salt
	 */
	public String getSalt() {
		return accountId.toString().concat(convertDateToString(create_ts));
	}

	/**
	 * Converts date object to String using yyyy-MM-dd HH:mm:ss 
	 * date format (the same as in UserService when creating hash)
	 * 
	 * @param date
	 * @return
	 */
	private String convertDateToString(Date date) {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		String result = date == null ? null : df.format(date);
		return result;
	}

}
