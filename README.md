# Museum Management System API

## About The Project

A comprehensive museum management system that provides features for museum owners to manage their museums, zones, artifacts, events, and tours, while allowing visitors to explore, book tickets, and interact with museums.

## Built With

*   [Spring Boot](https://spring.io/projects/spring-boot)
*   [Maven](https://maven.apache.org/)
*   [PostgreSQL](https://www.postgresql.org/)

## Usage

> **AuthsController**

1. **Register**: Create an account as visitor or museum owner with email verification
2. **Login**: Login with email and password for all roles
3. **Email Verification**: Verify account using OTP sent to email
4. **Google OAuth**: Login with Google for both visitor and museum owner roles
5. **Password Reset**: Complete forgot password flow with OTP verification
6. **OTP Management**: Resend OTP and check expiration status

> **MuseumsController**

1. **Get All Museums**: View all museums available to the public
2. **Get Museum by ID**: Get detailed information about a specific museum
3. **Get Nearby Museums**: Find approved museums filtered by distance
4. **Filter Museums**: Search museums with various filter options
5. **Approve Museum**: Admin functionality to approve museum registration requests
6. **Verify Booking**: Museum owner can verify visitor tickets by QR code or manual code

> **ZonesController**

1. **Create Zone**: Add new zones to organize museum areas
2. **Get All Zones**: View all museum zones
3. **Get Zone by ID**: Get detailed information about a specific zone
4. **Update Zone**: Modify zone details and information
5. **Delete Zone**: Remove zones from the museum
6. **Get Zone Categories**: View all available zone categories

> **ArtifactsController**

1. **Create Artifact**: Add new artifacts to specific zones
2. **Get All Artifacts**: View all museum artifacts
3. **Get Artifact by ID**: Get detailed information about a specific artifact
4. **Update Artifact**: Modify artifact details and information
5. **Patch Artifact**: Partial updates to artifact information
6. **Filter Artifacts**: Search artifacts with various filter options

> **EventsController**

1. **Create Event**: Museum owners can create new events
2. **Get All Events**: View all events from all museums (public access)
3. **Get Event by ID**: Get detailed information about a specific event
4. **Update Event**: Museum owners can modify their event details
5. **Delete Event**: Museum owners can soft delete their events
6. **Filter Events**: Search events with various filter options

> **ToursController**

1. **Get All Tours**: View all available tours for museum owners and visitors
2. **Get Tour by ID**: Get detailed information about a specific tour
3. **Accept Tour Request**: Museum owners can accept visitor tour requests
4. **Update Tour Status**: Update tour status to paid after visitor payment

> **BookingsController**

1. **Book Individual Ticket**: Visitors can book museum tickets
2. **Request Tour**: Visitors can request guided tours
3. **Get Booking History**: View all booking history with search and filter options
4. **Get Booking by ID**: Get detailed information about a specific booking
5. **Filter Bookings**: Search bookings with various filter options

> **ReviewsController**

1. **Create Review**: Visitors can write reviews for museums
2. **Get Museum Reviews**: View all reviews for a specific museum
3. **Update Review**: Modify existing reviews
4. **Delete Review**: Remove visitor reviews
5. **Get Review Statistics**: View statistical data for museum reviews

> **ProfilesController**

1. **Get Visitor Profile**: View visitor profile information
2. **Update Visitor Profile**: Modify visitor profile details
3. **Delete Visitor Profile**: Remove visitor account
4. **Get Museum Owner Profile**: View museum owner profile information
5. **Update Museum Owner Profile**: Modify museum owner profile details
6. **Update Payment Info**: Museum owners can update payment information
7. **Admin Profile Management**: Admin can view and update admin profiles

> **SchedulesController**

1. **Update Schedule**: Museum owners can set weekly schedules
2. **Get Weekly Schedule**: View 7-day schedule details for museums
3. **Get Grouped Schedule**: View organized schedule information
4. **Get Schedule by ID**: Get specific schedule details

> **GuidesController**

1. **Create Guide**: Add new tour guides
2. **Get All Guides**: View all available guides
3. **Update Guide**: Modify guide information
4. **Filter Guides**: Search guides with various filter options

> **TicketsInfoController**

1. **Get Ticket Information**: Museum owners can view their ticket information
2. **Update Ticket Information**: Museum owners can modify ticket pricing and details
3. **Get Ticket Info by Museum**: Get ticket information for specific museums

> **FavoritesController**

1. **Add to Favorites**: Visitors can add museums to their favorites
2. **Check Favorite Status**: View if a museum is in visitor's favorites

> **FilesController**

1. **Upload Files**: Upload images and documents for museums, artifacts, and events
2. **View Files**: Access uploaded files by filename
3. **Delete Files**: Remove uploaded files from the system

> **VisitorsController**

1. **Get Museum Visitors**: View visitor information for museums
2. **Get Admin Visitors**: Admin access to visitor data

> **MuseumCategoriesController**

1. **Get Museum Categories**: View all available museum categories

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
- **Museum Owners**: Can manage their museums and operations
- **Administrators**: Have system-wide management capabilities

## Security Features

- JWT-based authentication
- OTP verification for account security
- Google OAuth integration
- Role-based endpoint protection
- Secure file upload and management