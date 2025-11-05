package com.example.kok.service;

import com.example.kok.dto.*;
import com.example.kok.repository.*;
import com.example.kok.util.CompanyNoticeCriteria;
import com.example.kok.util.Criteria;
import com.example.kok.util.Search;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceNoticeServiceImpl implements ExperienceNoticeService {
    private final ExperienceNoticeDAO experienceNoticeDAO;
    private final FileService fileService;
    private final CompanyProfileFileDAO companyProfileFileDAO;
    private final S3Service s3Service;
    private final SaveExperienceNoticeDAO saveExperienceNoticeDAO;
    private final RequestExperienceDAO requestExperienceDAO;
    private final BannerFileDAO bannerFileDAO;

    @Override
    public ExperienceNoticeCriteriaDTO selectAllExperienceNotice(int page, Search search) {
        ExperienceNoticeCriteriaDTO experienceNoticeCriteriaDTO = new ExperienceNoticeCriteriaDTO();
        Criteria criteria = new Criteria(page, experienceNoticeDAO.findCountAll());
        List<ExperienceNoticeDTO> experiences=experienceNoticeDAO.findAll(criteria, search);
        experiences.forEach(experience -> {
            LocalDate endDate = LocalDate.parse(experience.getExperienceEndDate());
            LocalDate today = LocalDate.now();
            if (endDate.isBefore(today)) {
                long days = ChronoUnit.DAYS.between(today, endDate);
                experience.setRemainingDays(days);
            } else {
                experience.setRemainingDays(0L);
            }
            fileService.findFileByCompanyId(experience.getCompanyId())
                    .ifPresentOrElse(fileDTO -> {
                        experience.setFileName(fileDTO.getFileName());
                        experience.setFilePath(fileDTO.getFilePath());
                    }, ()->{
                        experience.setFileName("image.png");
                        experience.setFilePath("");
                    });
        });

        if(criteria.isHasMore()){
            experiences.remove(experiences.size() - 1);
        }

        experienceNoticeCriteriaDTO.setExperiences(experiences);
        experienceNoticeCriteriaDTO.setCriteria(criteria);
        return experienceNoticeCriteriaDTO;
    }

    @Override
    public void setPreSignedUrl(CompanyProfileFileDTO companyProfileFileDTO) {
        // 회사 ID로 파일 조회
        CompanyProfileFileDTO profile = companyProfileFileDAO.findFileByCompanyId(companyProfileFileDTO.getCompanyId());

        if (profile != null && profile.getFilePath() != null && !profile.getFilePath().isEmpty()) {
            String presignedUrl = s3Service.getPreSignedUrl(profile.getFilePath(), Duration.ofMinutes(5));
            companyProfileFileDTO.setFilePath(presignedUrl);
        } else {
            companyProfileFileDTO.setFilePath("/images/main-page/image.png");
        }
    }


    @Override
    @Cacheable(value = "experienceNoticeDTO", key = "#id")
    public ExperienceNoticeDTO findNoticeById(Long id) {
        ExperienceNoticeDTO result= experienceNoticeDAO.findById(id);
        String jobName= experienceNoticeDAO.findJobNameByID(id);
        result.setJobName(jobName);
        LocalDate endDate = LocalDate.parse(result.getExperienceNoticeEndDate());
            LocalDate today = LocalDate.now();
            if (!endDate.isBefore(today)) {
                long days = ChronoUnit.DAYS.between(today, endDate);
                result.setRemainingDays(days);
            } else {
                result.setRemainingDays(0L); // endDate보다 today가 이전일 경우 0
            }
            fileService.findFileByCompanyId(result.getCompanyId())
                    .ifPresentOrElse(fileDTO -> {
                        result.setFileName(fileDTO.getFileName());
                        result.setFilePath(fileDTO.getFilePath());
                    }, ()->{
                        result.setFileName("image.png");
                        result.setFilePath("");
                    });
        return result;
    }

    @Override
    public void saveExp(SaveExperienceNoticeDTO saveExperienceNoticeDTO) {
        saveExperienceNoticeDAO.saveExp(saveExperienceNoticeDTO);
    }

    @Override
    public void deleteExp(SaveExperienceNoticeDTO saveExperienceNoticeDTO) {
        saveExperienceNoticeDAO.deleteExp(saveExperienceNoticeDTO);
    }

    @Override
    public List<ExperienceNoticeDTO> findLatestFour() {
        List<ExperienceNoticeDTO> experiences = experienceNoticeDAO.findLatestFour();

        experiences.forEach(experience -> {
            LocalDate endDate = LocalDate.parse(experience.getExperienceEndDate());
            LocalDate today = LocalDate.now();

            if (endDate != null) {
                if (endDate.isBefore(today)) {
                    long days = ChronoUnit.DAYS.between(today, endDate);
                    experience.setRemainingDays(days);
                } else {
                    experience.setRemainingDays(0L);
                }
            } else {
                experience.setRemainingDays(0L);
            }

            fileService.findFileByCompanyId(experience.getCompanyId())
                    .ifPresentOrElse(fileDTO -> {
                        experience.setFileName(fileDTO.getFileName());
                        experience.setFilePath(fileDTO.getFilePath());
                    }, () -> {
                        experience.setFileName("image.png");
                        experience.setFilePath("/images/mypage/logo_1757380047672.webp"); // 임시 이미지 설정
                    });
        });
        return experiences;
    }

    @Override
    public boolean isSavedExp(SaveExperienceNoticeDTO saveExperienceNoticeDTO) {
        boolean result=saveExperienceNoticeDAO.idSavedExp(saveExperienceNoticeDTO);
        return result;
    }

    @Override
    public boolean isRequested(RequestExperienceDTO requestExperienceDTO) {
        boolean result=requestExperienceDAO.isRequested(requestExperienceDTO);
//        System.out.println("서비스 experienceNoticeId: "+requestExperienceDTO.getExperienceNoticeId()+"memberId: "+requestExperienceDTO.getMemberId());
        return result;
    }

    //    기업별 체험 공고
    @Override
    public CompanyExperienceNoticeCriteriaDTO getExperienceNoticesByCompanyId(int page, Long companyId, Search search) {
        int total = experienceNoticeDAO.findCountByCompanyId(companyId, search);
        CompanyNoticeCriteria criteria = new CompanyNoticeCriteria(page, total);
        List<ExperienceNoticeDTO> notices = experienceNoticeDAO.findAllByCompanyId(companyId, criteria, search);

        criteria.setHasMore(criteria.getPage() < criteria.getRealEnd());

        CompanyExperienceNoticeCriteriaDTO companyExperienceNoticeCriteriaDTO = new CompanyExperienceNoticeCriteriaDTO();
        companyExperienceNoticeCriteriaDTO.setCriteria(criteria);
        companyExperienceNoticeCriteriaDTO.setExperiences(notices);
        return companyExperienceNoticeCriteriaDTO;
    }

    @Override
    public List<ExperienceNoticeDTO> findLatestFourExperience() {
        List<ExperienceNoticeDTO> experienceNotices = experienceNoticeDAO.findLatestFour();

        experienceNotices.forEach(experienceNotice -> {
            if (experienceNotice.getFilePath() != null) {
                String preSignedUrl = s3Service.getPreSignedUrl(experienceNotice.getFilePath(), Duration.ofMinutes(10));
                experienceNotice.setFilePath(preSignedUrl);
            }
        });

        return experienceNotices;
    }

    @Override
    public String getBanner() {
        return bannerFileDAO.getBannerFileDTO()
                .map(banner -> {
                    String url = s3Service.getPreSignedUrl(banner.getBannerFilePath(), Duration.ofMinutes(10));
                    System.out.println("🎯 !!!!!Banner S3 URL: " + url);
                    return url;
                })
                .orElseGet(() -> {
                    System.out.println("!!!!!⚠️ BannerFileDTO not found, using default banner");
                    return "/images/experience/banner1.jpg";
                });
    }
}
