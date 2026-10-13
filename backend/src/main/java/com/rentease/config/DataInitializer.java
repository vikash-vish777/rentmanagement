package com.rentease.config;

import com.rentease.entity.*;
import com.rentease.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // 1. Create Default Users
        User admin = new User("Platform Admin", "admin@rentease.com", "+91 9876543210",
                passwordEncoder.encode("Admin@123"), Role.ROLE_ADMIN);
        userRepository.save(admin);

        User landlord1 = new User("Rajesh Sharma", "landlord@rentease.com", "+91 9822012345",
                passwordEncoder.encode("Landlord@123"), Role.ROLE_LANDLORD);
        userRepository.save(landlord1);

        User landlord2 = new User("Anita Deshmukh", "anita@rentease.com", "+91 9923456789",
                passwordEncoder.encode("Landlord@123"), Role.ROLE_LANDLORD);
        userRepository.save(landlord2);

        User tenant1 = new User("Pooja Patil", "tenant@rentease.com", "+91 9765467890",
                passwordEncoder.encode("Tenant@123"), Role.ROLE_TENANT);
        userRepository.save(tenant1);

        User tenant2 = new User("Amit Verma", "amit@rentease.com", "+91 9811122233",
                passwordEncoder.encode("Tenant@123"), Role.ROLE_TENANT);
        userRepository.save(tenant2);

        // 2. Create Properties
        // Property 1: Nashik College Road
        Property p1 = new Property();
        p1.setTitle("Spacious 2 BHK Apartment near College Road");
        p1.setDescription("Beautiful and well-ventilated 2 BHK apartment located in the prime residential hub of College Road. Close to top colleges, cafes, hospitals, and shopping centers. Features modular kitchen, wooden wardrobes, 24/7 water supply, elevator, and covered parking.");
        p1.setPropertyType(PropertyType.APARTMENT);
        p1.setRent(15000.0);
        p1.setSecurityDeposit(35000.0);
        p1.setAddress("402, Shivam Heights, Near BYK College, College Road");
        p1.setCity("Nashik");
        p1.setArea("College Road");
        p1.setPincode("422005");
        p1.setLatitude(20.0063);
        p1.setLongitude(73.7629);
        p1.setBedrooms(2);
        p1.setBathrooms(2);
        p1.setSqft(980);
        p1.setFurnishedStatus(FurnishedStatus.SEMI_FURNISHED);
        p1.setAvailableFrom(LocalDate.now().plusDays(2));
        p1.setStatus(PropertyStatus.AVAILABLE);
        p1.setOwner(landlord1);
        p1.addImage(new PropertyImage("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=1000&q=80", "Front Exterior", true, p1));
        p1.addImage(new PropertyImage("https://images.unsplash.com/photo-1586023492125-27b2c045efd7?auto=format&fit=crop&w=1000&q=80", "Living Room", false, p1));
        p1.addImage(new PropertyImage("https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=1000&q=80", "Master Bedroom", false, p1));
        p1.addImage(new PropertyImage("https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=1000&q=80", "Modular Kitchen", false, p1));
        propertyRepository.save(p1);

        // Property 2: Nashik Gangapur Road
        Property p2 = new Property();
        p2.setTitle("Luxury 3 BHK Independent Villa in Gangapur Road");
        p2.setDescription("Premium standalone 3 BHK villa with private lawn, spacious sunlit terrace, and high-end furnishings. Peaceful neighborhood with great security, clubhouse access, and lush green surroundings.");
        p2.setPropertyType(PropertyType.VILLA);
        p2.setRent(28000.0);
        p2.setSecurityDeposit(60000.0);
        p2.setAddress("Villa 12, Green Groves, Off Gangapur Road");
        p2.setCity("Nashik");
        p2.setArea("Gangapur Road");
        p2.setPincode("422013");
        p2.setLatitude(20.0210);
        p2.setLongitude(73.7445);
        p2.setBedrooms(3);
        p2.setBathrooms(3);
        p2.setSqft(1750);
        p2.setFurnishedStatus(FurnishedStatus.FURNISHED);
        p2.setAvailableFrom(LocalDate.now().plusDays(5));
        p2.setStatus(PropertyStatus.AVAILABLE);
        p2.setOwner(landlord1);
        p2.addImage(new PropertyImage("https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&w=1000&q=80", "Villa Exterior", true, p2));
        p2.addImage(new PropertyImage("https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=1000&q=80", "Grand Living Area", false, p2));
        p2.addImage(new PropertyImage("https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=1000&q=80", "Bedroom Suite", false, p2));
        propertyRepository.save(p2);

        // Property 3: Nashik Indira Nagar
        Property p3 = new Property();
        p3.setTitle("Affordable 1 BHK Flat in Indira Nagar");
        p3.setDescription("Clean and budget-friendly 1 BHK on 2nd floor, perfect for couples or small families. Quiet society with easy connectivity to Mumbai-Agra Highway and City Center Mall.");
        p3.setPropertyType(PropertyType.APARTMENT);
        p3.setRent(8500.0);
        p3.setSecurityDeposit(18000.0);
        p3.setAddress("Flat 204, Omkar Residency, Indira Nagar");
        p3.setCity("Nashik");
        p3.setArea("Indira Nagar");
        p3.setPincode("422009");
        p3.setLatitude(19.9675);
        p3.setLongitude(73.7744);
        p3.setBedrooms(1);
        p3.setBathrooms(1);
        p3.setSqft(560);
        p3.setFurnishedStatus(FurnishedStatus.SEMI_FURNISHED);
        p3.setAvailableFrom(LocalDate.now());
        p3.setStatus(PropertyStatus.AVAILABLE);
        p3.setOwner(landlord2);
        p3.addImage(new PropertyImage("https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=1000&q=80", "Living Space", true, p3));
        p3.addImage(new PropertyImage("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=1000&q=80", "Bedroom", false, p3));
        propertyRepository.save(p3);

        // Property 4: Pune Baner
        Property p4 = new Property();
        p4.setTitle("Modern Studio Apartment in High-Street Baner");
        p4.setDescription("Chic, fully-furnished studio apartment tailored for working professionals. High-speed fiber internet, AC, smart TV, washing machine, and 24/7 security concierge included.");
        p4.setPropertyType(PropertyType.STUDIO);
        p4.setRent(18000.0);
        p4.setSecurityDeposit(40000.0);
        p4.setAddress("Tower B-801, Urban Oasis, Baner High Street");
        p4.setCity("Pune");
        p4.setArea("Baner");
        p4.setPincode("411045");
        p4.setLatitude(18.5590);
        p4.setLongitude(73.7868);
        p4.setBedrooms(1);
        p4.setBathrooms(1);
        p4.setSqft(480);
        p4.setFurnishedStatus(FurnishedStatus.FURNISHED);
        p4.setAvailableFrom(LocalDate.now().plusDays(1));
        p4.setStatus(PropertyStatus.AVAILABLE);
        p4.setOwner(landlord2);
        p4.addImage(new PropertyImage("https://images.unsplash.com/photo-1502005229762-ae1b466420d2?auto=format&fit=crop&w=1000&q=80", "Studio Interior", true, p4));
        p4.addImage(new PropertyImage("https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=1000&q=80", "Kitchenette", false, p4));
        propertyRepository.save(p4);

        // Property 5: Pune Hinjawadi
        Property p5 = new Property();
        p5.setTitle("Premium 2 BHK Flat near IT Park, Hinjawadi");
        p5.setDescription("Gated community with gym, swimming pool, and badminton court. Walking distance to major IT companies in Phase 1 Hinjawadi. Spacious balcony overlooking green hills.");
        p5.setPropertyType(PropertyType.APARTMENT);
        p5.setRent(22000.0);
        p5.setSecurityDeposit(45000.0);
        p5.setAddress("6th Floor, Blue Ridge Township, Hinjawadi Phase 1");
        p5.setCity("Pune");
        p5.setArea("Hinjawadi");
        p5.setPincode("411057");
        p5.setLatitude(18.5913);
        p5.setLongitude(73.7389);
        p5.setBedrooms(2);
        p5.setBathrooms(2);
        p5.setSqft(1050);
        p5.setFurnishedStatus(FurnishedStatus.FURNISHED);
        p5.setAvailableFrom(LocalDate.now().plusDays(7));
        p5.setStatus(PropertyStatus.AVAILABLE);
        p5.setOwner(landlord1);
        p5.addImage(new PropertyImage("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=1000&q=80", "Building View", true, p5));
        p5.addImage(new PropertyImage("https://images.unsplash.com/photo-1507089947368-19c1da9775ae?auto=format&fit=crop&w=1000&q=80", "Living Room", false, p5));
        propertyRepository.save(p5);

        // Property 6: Nashik Panchavati
        Property p6 = new Property();
        p6.setTitle("Cozy 1 RK Room for Students in Panchavati");
        p6.setDescription("Clean 1 RK room close to educational institutions and market. Ideal for students and single professionals. Attached washroom and separate utility balcony.");
        p6.setPropertyType(PropertyType.ROOM);
        p6.setRent(5500.0);
        p6.setSecurityDeposit(10000.0);
        p6.setAddress("12, Ramkunj Apt, Panchavati Karanja");
        p6.setCity("Nashik");
        p6.setArea("Panchavati");
        p6.setPincode("422003");
        p6.setLatitude(20.0112);
        p6.setLongitude(73.7915);
        p6.setBedrooms(1);
        p6.setBathrooms(1);
        p6.setSqft(320);
        p6.setFurnishedStatus(FurnishedStatus.UNFURNISHED);
        p6.setAvailableFrom(LocalDate.now());
        p6.setStatus(PropertyStatus.AVAILABLE);
        p6.setOwner(landlord2);
        p6.addImage(new PropertyImage("https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=1000&q=80", "Room Space", true, p6));
        propertyRepository.save(p6);

        // Property 7: Mumbai Bandra West
        Property p7 = new Property();
        p7.setTitle("Sea Breeze 3 BHK Apartment in Bandra West");
        p7.setDescription("Ultra-exclusive sea-view apartment in prime Bandra. Designer interiors, Italian marble flooring, VRV air conditioning, and top-tier security.");
        p7.setPropertyType(PropertyType.APARTMENT);
        p7.setRent(75000.0);
        p7.setSecurityDeposit(150000.0);
        p7.setAddress("14th Floor, Skyline Towers, Perry Road, Bandra West");
        p7.setCity("Mumbai");
        p7.setArea("Bandra West");
        p7.setPincode("400050");
        p7.setLatitude(19.0596);
        p7.setLongitude(72.8295);
        p7.setBedrooms(3);
        p7.setBathrooms(3);
        p7.setSqft(1450);
        p7.setFurnishedStatus(FurnishedStatus.FURNISHED);
        p7.setAvailableFrom(LocalDate.now().plusDays(10));
        p7.setStatus(PropertyStatus.AVAILABLE);
        p7.setOwner(landlord1);
        p7.addImage(new PropertyImage("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1000&q=80", "Luxury Apartment", true, p7));
        p7.addImage(new PropertyImage("https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1000&q=80", "Dining & Hall", false, p7));
        propertyRepository.save(p7);

        // 3. Seed Reviews
        Review r1 = new Review(p1, tenant1, 5, "Amazing location on College Road! Landlord is very supportive and the house is spotless.");
        Review r2 = new Review(p1, tenant2, 4, "Great society and 24hr water backup. Highly recommend for families.");
        Review r3 = new Review(p2, tenant1, 5, "The villa is breathtaking. Wonderful garden and spacious rooms.");
        reviewRepository.save(r1);
        reviewRepository.save(r2);
        reviewRepository.save(r3);

        // 4. Seed Bookings
        Booking b1 = new Booking(p1, tenant1, landlord1, LocalDate.now().plusDays(5), "We are a working couple looking to move in next week. Can we visit on Saturday?");
        b1.setStatus(BookingStatus.PENDING);
        bookingRepository.save(b1);

        Booking b2 = new Booking(p3, tenant2, landlord2, LocalDate.now().plusDays(3), "Looking to rent for 11 months agreement. Please confirm availability.");
        b2.setStatus(BookingStatus.ACCEPTED);
        b2.setLandlordNote("Welcome! Please bring ID proofs and deposit cheque during visit.");
        bookingRepository.save(b2);

        // 5. Seed Favorites
        favoriteRepository.save(new Favorite(tenant1, p1));
        favoriteRepository.save(new Favorite(tenant1, p2));

        // 6. Seed Messages
        Message m1 = new Message(tenant1, landlord1, p1, "Hello Rajesh ji, is this 2 BHK apartment on College Road available for visiting tomorrow?");
        m1.setCreatedAt(LocalDateTime.now().minusHours(3));
        Message m2 = new Message(landlord1, tenant1, p1, "Hello Pooja! Yes, you can visit tomorrow around 4 PM. Call me when you arrive.");
        m2.setCreatedAt(LocalDateTime.now().minusHours(2));
        messageRepository.save(m1);
        messageRepository.save(m2);

        System.out.println(">>> RentEase demo dataset initialized successfully! <<<");
    }
}
