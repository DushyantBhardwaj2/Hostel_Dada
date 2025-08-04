import { User } from 'firebase/auth'

/**
 * Validates if the user's email belongs to the allowed domain
 * @param user - Firebase User object
 * @returns boolean indicating if the email domain is valid
 */
export function validateEmailDomain(user: User): boolean {
  if (!user.email) return false
  
  const allowedDomain = 'nsut.ac.in'
  const emailDomain = user.email.split('@')[1]
  
  return emailDomain === allowedDomain
}

/**
 * Gets a user-friendly error message for domain validation
 * @returns string with the error message
 */
export function getDomainValidationError(): string {
  return 'Access restricted to NSUT email addresses only. Please use your @nsut.ac.in email.'
}

/**
 * Validates email domain during sign-up
 * @param email - Email address to validate
 * @returns boolean indicating if the email domain is valid
 */
export function validateEmailDomainBeforeSignup(email: string): boolean {
  const allowedDomain = 'nsut.ac.in'
  const emailDomain = email.split('@')[1]
  
  return emailDomain === allowedDomain
}
