package aarav.kharade.addharux.civic.enrollment

object EnrollmentValidator {
    fun validate(data: EnrollmentData): List<String> {
        val errors = mutableListOf<String>()
        
        if (data.fullName.trim().length < 3) {
            errors.add("Name must be at least 3 characters long")
        }
        
        if (!data.dateOfBirth.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) {
            errors.add("Date of birth must be in YYYY-MM-DD format")
        }
        
        if (data.address.trim().length < 10) {
            errors.add("Address must be at least 10 characters long")
        }
        
        if (!data.contactNumber.matches(Regex("""^\+?[0-9]{10,14}$"""))) {
            errors.add("Contact number is invalid")
        }
        
        return errors
    }
}
