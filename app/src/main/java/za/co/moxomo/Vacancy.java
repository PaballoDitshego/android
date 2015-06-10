package za.co.moxomo;

import org.joda.time.DateTime;
import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.util.Date;




@Parcel
public class Vacancy  {
	

	public Long id;
	public String job_title;
	public String description;
	public String ad_id;

	public String getAd_id() {
		return ad_id;
	}

	public void setAd_id(String ad_id) {
		this.ad_id = ad_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAdvertDate(DateTime advertDate) {
		this.advertDate = advertDate;
	}

	public String company_name;

	String tweet;

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}


	public String ref_number;
	public String location;
	public String province;

	public String min_qual;
	public String category;

	public String duties;

	public String key_competencies;
	@ParcelPropertyConverter(JodaDateTimeConverter.class)
	public DateTime advertDate;
	@ParcelPropertyConverter(JodaDateTimeConverter.class)
	public DateTime closingDate;
	public String website;
	
	public String imageUrl;
	public Long company_id;
	public Long agent_id;
	public String status;

	public String remuneration;
	
	public String source;
	

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getMin_qual() {
		return min_qual;
	}

	public void setMin_qual(String min_qual) {
		this.min_qual = min_qual;
	}

	public String getKey_competencies() {
		return key_competencies;
	}

	public void setKey_competencies(String key_competencies) {
		this.key_competencies = key_competencies;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getRemuneration() {
		return remuneration;
	}

	public void setRemuneration(String remuneration) {
		this.remuneration = remuneration;
	}

	

	public String uniqueName = agent_id + "_" + new Date().getTime();


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public void setCompany_id(Long company_id) {
		this.company_id = company_id;
	}

	public void setAgent_id(Long agent_id) {
		this.agent_id = agent_id;
	}



	public String getJob_title() {
		return job_title;
	}

	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}

	public String getRecruiter_name() {
		return company_name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setRecruiter_name(String recruiter_name) {
		this.company_name = recruiter_name;
	}

	public String getRef_number() {
		return ref_number;
	}

	public void setRef_number(String ref_number) {
		this.ref_number = ref_number;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getMin_qualifications() {
		return min_qual;
	}

	public void setMin_qualifications(String min_qualifications) {
		this.min_qual = min_qualifications;
	}

	public String getDuties() {
		return duties;
	}

	public void setDuties(String duties) {
		this.duties = duties;
	}

	public String getCompetencies() {
		return key_competencies;
	}

	public void setCompetencies(String competencies) {
		this.key_competencies = competencies;
	}

	public DateTime getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(DateTime closingDate) {
		this.closingDate = closingDate;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getImageURL() {
		return imageUrl;
	}

	public void setImageURL(String imageURL) {
		this.imageUrl = imageURL;
	}

	public Long getCompany_id() {
		return company_id;
	}

	public Long getAgent_id() {
		return agent_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public DateTime getAdvertDate() {
		return advertDate;
	}



	

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
