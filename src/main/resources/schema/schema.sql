CREATE TABLE user_info(
    user_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    email VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(14) CHECK ( role IN ('ROLE_ADMIN', 'ROLE_MUSEUM_OWNER', 'ROLE_VISITOR') ),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE subscriptions (
    subscription_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    user_id UUID,
    FOREIGN KEY (user_id) REFERENCES user_info(user_id) ON DELETE CASCADE,
    subscription_code VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE otps (
    otp_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    user_id UUID,
    otp_code VARCHAR(6),
    expired_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_info(user_id) ON DELETE CASCADE
);

CREATE TABLE notification_message (
    notification_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    subscription_id UUID,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(subscription_id) ON DELETE CASCADE,
    title VARCHAR(255),
    message TEXT,
    is_read BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admin (
    admin_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    user_id UUID,
    FOREIGN KEY (user_id) REFERENCES user_info(user_id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    profile_image_link VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE museum_categories (
    museum_category_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE museum_owners (
    museum_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    user_id UUID,
    FOREIGN KEY (user_id) REFERENCES user_info(user_id) ON DELETE CASCADE,
    museum_category_id UUID,
    FOREIGN KEY (museum_category_id) REFERENCES museum_categories(museum_category_id),
    name VARCHAR(255) NOT NULL,
    contact_number VARCHAR(50),
    lat NUMERIC(9,6),
    lng NUMERIC(9,6),
    logo VARCHAR(255),
    banner VARCHAR(255),
    landscapes JSONB,
    description TEXT,
    is_approved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE visitors (
    visitor_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    user_id UUID,
    FOREIGN KEY (user_id) REFERENCES user_info(user_id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    contact_number VARCHAR(50),
    gender VARCHAR(7) CHECK (gender IN ('Male', 'Female')),
    dob DATE,
    profile_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE schedules (
    schedule_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    museum_id UUID,
    FOREIGN KEY (museum_id) REFERENCES museum_owners(museum_id) ON DELETE CASCADE,
    day VARCHAR(10) CHECK (day IN ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday')),
    opening_time TIME,
    closing_time TIME,
    day_off BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE events (
    event_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    museum_id UUID,
    FOREIGN KEY (museum_id) REFERENCES museum_owners(museum_id),
    title VARCHAR(255),
    sub_title VARCHAR(255),
    content TEXT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    image_links JSONB,
    curator VARCHAR(255),
    accessibility_note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE zone_categories (
    zone_category_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE museum_zones (
    museum_zone_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    museum_id UUID,
    FOREIGN KEY (museum_id) REFERENCES museum_owners(museum_id) ON DELETE CASCADE,
    zone_category_id UUID,
    FOREIGN KEY (zone_category_id) REFERENCES zone_categories(zone_category_id),
    name VARCHAR(255),
    description VARCHAR(255),
    picture_link VARCHAR(255),
    video_link VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE bookings (
    booking_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    museum_id UUID,
    FOREIGN KEY (museum_id) REFERENCES museum_owners(museum_id) ON DELETE CASCADE,
    visitor_id UUID,
    FOREIGN KEY (visitor_id) REFERENCES visitors(visitor_id) ON DELETE CASCADE,
    ticket_price NUMERIC(10,2),
    ticket_type VARCHAR(10) CHECK (ticket_type IN ('Local', 'Foreigner')),
    ticket_status VARCHAR(8) CHECK (ticket_status IN ('Valid', 'Used', 'Expired')),
    booking_type VARCHAR(11) CHECK (booking_type IN ('Individual', 'Tour')),
    slot_amount INTEGER,
    booking_date TIMESTAMP,
    expired_date TIMESTAMP,
    qr_code VARCHAR(255),
    total_price NUMERIC(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reviews (
    review_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    museum_id UUID,
    FOREIGN KEY (museum_id) REFERENCES museum_owners(museum_id) ON DELETE CASCADE,
    visitor_id UUID,
    FOREIGN KEY (visitor_id) REFERENCES visitors(visitor_id) ON DELETE CASCADE,
    comment TEXT,
    rating NUMERIC(2,1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE favorites (
    favorite_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    museum_id UUID,
    FOREIGN KEY (museum_id) REFERENCES museum_owners(museum_id) ON DELETE CASCADE,
    visitor_id UUID,
    FOREIGN KEY (visitor_id) REFERENCES visitors(visitor_id) ON DELETE CASCADE,
    is_favorite BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE guides(
    guide_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    museum_id UUID,
    FOREIGN KEY (museum_id) REFERENCES museum_owners(museum_id) ON DELETE CASCADE,
    guide_name VARCHAR(255),
    contact_number VARCHAR(25),
    static_qr_link VARCHAR(255),
    is_available BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tours (
    tour_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    booking_id UUID,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    tour_price NUMERIC(10,2),
    status VARCHAR(8) CHECK (status IN ('Request', 'Pending', 'Paid')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tour_guides (
    tour_guide_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    tour_id UUID,
    FOREIGN KEY (tour_id) REFERENCES tours(tour_id),
    guide_id UUID,
    FOREIGN KEY (guide_id) REFERENCES guides(guide_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE artifacts (
    artifact_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    museum_zone_id UUID,
    FOREIGN KEY (museum_zone_id) REFERENCES museum_zones(museum_zone_id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    third_d_model_link VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

