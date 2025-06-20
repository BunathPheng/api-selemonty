# Museum Management System API

# Account For Testing
1. Admin: 
   email: selamonty.team@gmail.com
   password: @M1nBtb007
2. Museum Owner
   email: test@gmail.com
   password: Test123@
3. Visitor:
   email: good123vichet@gmail.com
   password: Test123@

## About The Project

A comprehensive museum management system that provides features for museum owners to manage their museums, zones, artifacts, events, and tours, while allowing visitors to explore, book tickets, and interact with museums.

## Built With

*   [Spring Boot](https://spring.io/projects/spring-boot)
*   [Maven](https://maven.apache.org/)
*   [PostgreSQL](https://www.postgresql.org/)

## 🌐 API Access

You can access the API at the following URL:http://34.129.192.53:8080/swagger-ui/index.html

## Usage

> **AuthsController**

1. **Register**: Register as visitor or museum owner role with email verification
2. **Login**: Use for login for all roles with email and password
3. **Verify Account**: Verify account using OTP sent to email
4. **Google Sign-in**: Login with Google using IdToken for both visitor and museum owner roles
5. **Resend OTP**: Send re-send OTP to verify account
6. **Forgot Password**: Complete forgot password feature with OTP verification
7. **Reset Password**: Reset password after confirming all steps
8. **Change Password**: Use old password to change to new password
9. **OTP Expiration**: Check OTP expiration status

> **MuseumsController**

1. **Get All Museums**: Get all museums - allowed for guests
2. **Get Museum by ID**: Get museum by museum ID - allowed for guests
3. **Get Nearby Museums**: Get all approved museums filtered by distance - allowed for all roles and guests
4. **Filter Museums**: Get all museums with filter options - allowed for guests
5. **Approve Museum**: Approve request museums - allowed only for admin
6. **Verify QR Booking**: Check and verify booking ticket by bookingId through QR scan - only museum owner can use
7. **Verify Code Booking**: Check and verify booking ticket by code instead of scanning - only museum owner can use

> **ZonesController**

1. **Create Zone**: Create museum zone for organizing museum areas
2. **Get All Zones**: Get all museum zones
3. **Get Zone by ID**: Get museum zone detail by museum zone ID
4. **Update Zone**: Update museum zone detail by zone ID
5. **Delete Zone**: Delete museum zone by zone ID
6. **Get Zone Categories**: Get all museum zone categories

> **ArtifactsController**

1. **Create Artifact**: Add museum artifact by zone ID
2. **Get All Artifacts**: Get all museum artifacts
3. **Get Artifact by ID**: Get museum artifact by artifact ID
4. **Update Artifact**: Update museum artifact by artifact ID
5. **Patch Artifact**: Partially update museum artifact by artifact ID
6. **Filter Artifacts**: Get museum artifacts with filter options

> **EventsController**

1. **Create Event**: Create a new event - for museum owner role only
2. **Get All Events**: Get all events of all museums - can use without authorization
3. **Get Event by ID**: Get event by event ID
4. **Update Event**: Update an event by event ID - for museum owner role only
5. **Delete Event**: Soft delete an event by updating delete status - for museum owner role only
6. **Filter Events**: Get events with filter options

> **ToursController**

1. **Get All Tours**: Get all tours - for museum owner and visitor
2. **Get Tour by ID**: Get tour by tour ID - for museum owner and visitor
3. **Accept Tour**: Accept request tour - for museum owner and visitor
4. **Update Tour Status**: Update tour status to paid after visitor payment

> **BookingsController**

1. **Request Tour**: Request tour booking - only visitor can use
2. **Book Individual Ticket**: Book individual museum ticket - only visitor can use
3. **Get Booking History**: Get all booking history with search, category and date range - museum owner and visitor can use
4. **Get Booking by ID**: Get booking by booking ID with category and date filters - museum owner and visitor can use
5. **Filter Bookings**: Get bookings with filter options

> **ReviewsController**

1. **Create Review**: Create a review for a museum
2. **Get Museum Reviews**: Get all reviews of a museum
3. **Update Review**: Update a review of a museum
4. **Delete Review**: Delete visitor review for a museum
5. **Get Review Statistics**: Get review statistics for a museum

> **ProfilesController**

1. **Get Visitor Profile**: Get visitor profile information
2. **Update Visitor Profile**: Update visitor profile - for only visitor
3. **Delete Visitor Profile**: Delete visitor profile
4. **Get Museum Owner Profile**: Get museum profile - for only museum owner
5. **Update Museum Owner Profile**: Update museum profile - for only museum owner
6. **Update Payment Info**: Update museum payment information - for only museum owner
7. **Get Admin Profile**: Get admin profile information
8. **Update Admin Profile**: Insert and update admin profile - any field can be null if don't want to update

> **SchedulesController**

1. **Update Schedule**: Update schedule for any day of a week
2. **Get Weekly Schedule Detail**: Get schedule of a week with 7 days
3. **Get Museum Schedule Detail**: Get schedule of a week with 7 days by museum ID
4. **Get Grouped Schedule**: Get grouped schedule by museum ID
5. **Get Schedule by ID**: Get schedule by schedule ID

> **GuidesController**

1. **Create Guide**: Create new tour guide
2. **Get All Guides**: Get all available guides
3. **Update Guide**: Update guide information by guide ID
4. **Filter Guides**: Get guides with filter options

> **TicketsInfoController**

1. **Get Ticket Information**: Get ticket information for museum owner role without required museum ID
2. **Update Ticket Information**: Update ticket information for museum owner role
3. **Get Ticket Info by Museum**: Get ticket information by museum ID - for visitor and museum owner roles

> **FavoritesController**

1. **Check Favorite**: Check if visitor has added museum as favorite
2. **Add to Favorites**: Visitor add museum as their favorite

> **FilesController**

1. **Upload File**: Upload files for museums, artifacts, and events
2. **View File**: View uploaded file by filename
3. **Delete File**: Delete uploaded file by filename

> **VisitorsController**

1. **Get Museum Visitors**: Get visitor information for museums
2. **Get Admin Visitors**: Get visitor information for admin access

> **MuseumCategoriesController**

1. **Get Museum Categories**: Get all available museum categories

## What we have done

*   [AuthsController](#) **100%**
*   [MuseumsController](#) **100%**
*   [ZonesController](#) **100%**
*   [ArtifactsController](#) **100%**
*   [EventsController](#) **100%**
*   [ToursController](#) **100%**
*   [BookingsController](#) **100%**
*   [ReviewsController](#) **100%**
*   [ProfilesController](#) **100%**
*   [SchedulesController](#) **100%**
*   [GuidesController](#) **100%**
*   [TicketsInfoController](#) **100%**
*   [FavoritesController](#) **100%**
*   [FilesController](#) **100%**
*   [VisitorsController](#) **100%**
*   [MuseumCategoriesController](#) **100%**

## API Features

### For Visitors
- Browse and explore museums
- Book tickets and request tours
- Write and manage reviews
- Add museums to favorites
- Manage personal profiles
- View events and schedules

### For Museum Owners
- Manage museum information and profiles
- Create and organize zones and artifacts
- Schedule museum operations
- Handle bookings and verify tickets
- Create and manage events
- View visitor analytics and reviews

### For Administrators
- Approve museum registration requests
- Manage user profiles and permissions
- Oversee system operations

## Role-Based Access Control

The system implements comprehensive role-based access control with three main user types:
- **Visitors**: Can explore, book, and review museums
- **Museum Owners**: Can manage their museums and operations (requires admin approval)
- **Administrators**: Have system-wide management capabilities

## Security Features

- JWT-based authentication
- OTP verification for account security
- Google OAuth integration
- Role-based endpoint protection
- Secure file upload and management