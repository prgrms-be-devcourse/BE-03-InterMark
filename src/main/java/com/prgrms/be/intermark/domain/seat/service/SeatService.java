package com.prgrms.be.intermark.domain.seat.service;

import com.prgrms.be.intermark.domain.seat.model.Seat;
import com.prgrms.be.intermark.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public Seat findById(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("존재하지 않는 좌석입니다");
                });
    }
}
