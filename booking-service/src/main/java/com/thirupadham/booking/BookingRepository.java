package com.thirupadham.booking;

import org.springframework.data.jpa.repository.JpaRepository;

// This interface looks empty, but Spring Data JPA generates a full working
// implementation behind the scenes at startup - save(), findAll(), findById(),
// delete(), etc. all come for free just by extending JpaRepository.
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
